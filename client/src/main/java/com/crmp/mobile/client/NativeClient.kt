package com.crmp.mobile.client

/**
 * JNI bridge to the native CRMP/SA-MP client stub.
 *
 * TODO: wire a real network/game client (RakNet / custom protocol),
 *       load game data paths, audio/render loop, etc.
 *       Do NOT ship proprietary third-party .so binaries here.
 */
object NativeClient {

    const val RESULT_OK = 0
    const val RESULT_NOT_IMPLEMENTED = 1
    const val RESULT_INVALID_ARGS = 2
    const val RESULT_NATIVE_ERROR = 3

    @Volatile
    private var libraryLoaded: Boolean? = null

    /**
     * Lazily load the native library inside call sites — never during object init —
     * so a missing/unloadable .so cannot crash the process at class load time.
     */
    private fun ensureLibraryLoaded(): Boolean {
        libraryLoaded?.let { return it }
        return synchronized(this) {
            libraryLoaded?.let { return it }
            val ok = try {
                System.loadLibrary("crmp_client")
                true
            } catch (t: Throwable) {
                // Allows unit tests / JVM hosts / devices without matching ABI .so
                false
            }
            libraryLoaded = ok
            ok
        }
    }

    /**
     * Requests a connection to [server] (host:port) with [nick].
     *
     * @return result code; currently the C++ stub returns [RESULT_NOT_IMPLEMENTED].
     */
    @JvmStatic
    fun launch(server: String, nick: String): Int {
        if (server.isBlank() || nick.isBlank()) return RESULT_INVALID_ARGS
        if (!ensureLibraryLoaded()) return RESULT_NOT_IMPLEMENTED
        return try {
            nativeLaunch(server, nick)
        } catch (t: Throwable) {
            RESULT_NATIVE_ERROR
        }
    }

    /**
     * Optional: query native library build id / stub version.
     */
    @JvmStatic
    fun nativeVersion(): String {
        if (!ensureLibraryLoaded()) return "jvm-stub"
        return try {
            nativeGetVersion()
        } catch (t: Throwable) {
            "native-error"
        }
    }

    private external fun nativeLaunch(server: String, nick: String): Int
    private external fun nativeGetVersion(): String
}
