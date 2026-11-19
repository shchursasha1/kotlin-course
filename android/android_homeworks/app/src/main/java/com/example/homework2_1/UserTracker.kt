package com.example.homework2_1

import android.content.Context

// Singleton lives as long as the process, so it must not keep old Activities
object UserTracker {
    private val listeners = mutableListOf<Context>()

    fun register(context: Context) {
        listeners.add(context)
    }

    // Called in onDestroy(), otherwise the destroyed Activity stays in memory (leak)
    fun unregister(context: Context) {
        listeners.remove(context)
    }

    fun getListenersCount(): Int = listeners.size
}
