package com.example.homework2_1

import android.app.Application
import timber.log.Timber

// Created once when the app process starts, before any Activity
class MyApp : Application() {

    // Lives only while the process is alive, resets to 0 after the process is killed
    var startCount = 0

    override fun onCreate() {
        super.onCreate()

        // Turn on Timber logs only in debug build
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("MyApp: onCreate() викликано")
    }
}
