package com.example.homework2_1

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
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

        // Foreground / background of the whole app
        ProcessLifecycleOwner.get().lifecycle.addObserver(BackgroundDetector())

        // Called for every Activity in the app
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                logState("onActivityCreated", activity)
            }

            override fun onActivityStarted(activity: Activity) {
                logState("onActivityStarted", activity)
            }

            override fun onActivityResumed(activity: Activity) {
                logState("onActivityResumed", activity)
            }

            override fun onActivityPaused(activity: Activity) {
                logState("onActivityPaused", activity)
            }

            override fun onActivityStopped(activity: Activity) {
                logState("onActivityStopped", activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                logState("onActivityDestroyed", activity)
            }
        })
    }

    private fun logState(event: String, activity: Activity) {
        // Plain Activity has no lifecycle, AppCompatActivity is a LifecycleOwner
        val state = (activity as? LifecycleOwner)?.lifecycle?.currentState
        Timber.d("$event: ${activity.javaClass.simpleName}, state = $state")
    }
}
