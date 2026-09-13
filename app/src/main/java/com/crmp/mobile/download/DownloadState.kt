package com.crmp.mobile.download

/**
 * Состояние загрузки игрового кэша.
 */
sealed class DownloadState {
    data object Idle : DownloadState()

    data class Running(
        val bytesRead: Long,
        val totalBytes: Long,
        /** 0–100, или -1 если размер неизвестен */
        val percent: Int,
    ) : DownloadState()

    data class Success(val localPath: String) : DownloadState()

    data class Error(val message: String) : DownloadState()
}
