package com.crmp.mobile

import android.app.Application
import android.util.Log

/**
 * Application entry for cold-start hardening and OEM crash diagnostics.
 */
class CrmpApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "Uncaught on ${thread.name}", throwable)
            previous?.uncaughtException(thread, throwable)
        }
        Log.i(TAG, "CrmpApp onCreate")
    }

    companion object {
        const val TAG = "CRMP"
    }
}
