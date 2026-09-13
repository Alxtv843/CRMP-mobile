package com.crmp.mobile.download

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.coroutines.coroutineContext

/**
 * Скачивает пользовательский кэш по HTTPS/HTTP в app-specific external storage.
 * Не содержит и не раздаёт ассеты GTA SA — только URL, заданный пользователем.
 */
class CacheDownloader(context: Context) {

    private val appContext = context.applicationContext

    /** Каталог кэша: Android/data/<pkg>/files/game_cache */
    fun cacheDir(): File {
        val base = appContext.getExternalFilesDir(null) ?: appContext.filesDir
        return File(base, "game_cache").also { if (!it.exists()) it.mkdirs() }
    }

    fun localStatus(): CacheStatus {
        val dir = cacheDir()
        val marker = File(dir, MARKER_NAME)
        val ready = marker.exists() && dir.listFiles()?.any { it.name != MARKER_NAME } == true
        return CacheStatus(
            path = dir.absolutePath,
            isDownloaded = ready,
            sizeBytes = if (ready) {
                dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
            } else {
                0L
            },
        )
    }

    /**
     * Скачивает по [url] с перезаписью каталога. Отмена — через отмену корутины.
     * Если URL оканчивается на .zip — распаковывает в каталог кэша.
     */
    suspend fun downloadCancellable(
        url: String,
        onProgress: (bytesRead: Long, totalBytes: Long) -> Unit,
    ): Result<String> = withContext(Dispatchers.IO) {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) {
            return@withContext Result.failure(DownloadException("URL кэша не указан"))
        }
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return@withContext Result.failure(
                DownloadException("Неверный URL (нужен http:// или https://)"),
            )
        }

        val destDir = cacheDir()
        clearDir(destDir)
        destDir.mkdirs()

        val fileName = guessFileName(trimmed)
        val destFile = File(destDir, fileName)
        val partialFile = File(destDir, "$fileName.partial")

        try {
            val conn = (URL(trimmed).openConnection() as HttpURLConnection).apply {
                connectTimeout = 30_000
                readTimeout = 60_000
                instanceFollowRedirects = true
                requestMethod = "GET"
                setRequestProperty("User-Agent", "CRMP-Mobile/0.1.4")
            }
            try {
                val code = conn.responseCode
                if (code !in 200..299) {
                    throw DownloadException("Сервер вернул код $code")
                }
                val total = conn.contentLengthLong.let { if (it < 0) -1L else it }
                BufferedInputStream(conn.inputStream).use { input ->
                    FileOutputStream(partialFile).use { output ->
                        val buf = ByteArray(8192)
                        var readTotal = 0L
                        var lastEmit = 0L
                        while (true) {
                            coroutineContext.ensureActive()
                            val n = input.read(buf)
                            if (n < 0) break
                            output.write(buf, 0, n)
                            readTotal += n
                            if (readTotal - lastEmit >= 64 * 1024 ||
                                (total > 0 && readTotal >= total)
                            ) {
                                onProgress(readTotal, total)
                                lastEmit = readTotal
                            }
                        }
                        onProgress(readTotal, if (total > 0) total else readTotal)
                    }
                }
            } finally {
                conn.disconnect()
            }

            if (partialFile.exists()) {
                if (destFile.exists()) destFile.delete()
                if (!partialFile.renameTo(destFile)) {
                    partialFile.copyTo(destFile, overwrite = true)
                    partialFile.delete()
                }
            }

            val pathNoQuery = trimmed.substringBefore('?')
            val isZip = fileName.endsWith(".zip", ignoreCase = true) ||
                pathNoQuery.endsWith(".zip", ignoreCase = true)
            if (isZip && destFile.exists()) {
                try {
                    unzip(destFile, destDir)
                    destFile.delete()
                } catch (e: Exception) {
                    throw DownloadException(
                        "Не удалось распаковать архив: ${e.message ?: ""}".trim(),
                    )
                }
            }

            File(destDir, MARKER_NAME).writeText(
                "url=$trimmed\ndownloadedAt=${System.currentTimeMillis()}\n",
            )
            Result.success(destDir.absolutePath)
        } catch (ce: kotlinx.coroutines.CancellationException) {
            partialFile.delete()
            throw ce
        } catch (e: DownloadException) {
            partialFile.delete()
            Result.failure(e)
        } catch (_: java.net.UnknownHostException) {
            partialFile.delete()
            Result.failure(DownloadException("Нет сети или хост недоступен"))
        } catch (_: java.net.SocketTimeoutException) {
            partialFile.delete()
            Result.failure(DownloadException("Таймаут соединения"))
        } catch (e: java.io.IOException) {
            partialFile.delete()
            Result.failure(
                DownloadException("Ошибка загрузки: ${e.message ?: "запись/сеть"}"),
            )
        } catch (e: Exception) {
            partialFile.delete()
            Result.failure(
                DownloadException("Ошибка: ${e.message ?: e.javaClass.simpleName}"),
            )
        }
    }

    private fun unzip(zipFile: File, destDir: File) {
        ZipInputStream(BufferedInputStream(zipFile.inputStream())).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val outFile = File(destDir, entry.name).canonicalFile
                val destCanon = destDir.canonicalPath
                if (!outFile.path.startsWith(destCanon + File.separator) &&
                    outFile.path != destCanon
                ) {
                    throw SecurityException("Недопустимый путь в архиве")
                }
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { zis.copyTo(it) }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    private fun clearDir(dir: File) {
        if (!dir.exists()) return
        dir.listFiles()?.forEach { f ->
            if (f.isDirectory) f.deleteRecursively() else f.delete()
        }
    }

    private fun guessFileName(url: String): String {
        val path = try {
            URL(url).path
        } catch (_: Exception) {
            url.substringAfterLast('/')
        }
        val name = path.substringAfterLast('/').substringBefore('?').ifBlank { "cache.bin" }
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_").ifBlank { "cache.bin" }
    }

    companion object {
        const val MARKER_NAME = ".crmp_cache_ready"
    }
}

data class CacheStatus(
    val path: String,
    val isDownloaded: Boolean,
    val sizeBytes: Long,
)

class DownloadException(message: String) : Exception(message)
