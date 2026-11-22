package com.example.homework2_1.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Hybrid service: Foreground (works when the app is hidden) + Bound (the screen reads the seconds)
class TimerForegroundService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null

    private val _timerSeconds = MutableStateFlow(0)
    fun getTimerFlow(): StateFlow<Int> = _timerSeconds.asStateFlow()

    // Gives the Activity direct access to this service object (same process)
    inner class LocalBinder : Binder() {
        fun getService(): TimerForegroundService = this@TimerForegroundService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    // Called by ContextCompat.startForegroundService()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Таймер працює")
            .setContentText("Відлік часу триває у фоні")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .build()

        // Must be called right away, otherwise the system stops the service
        startForeground(NOTIFICATION_ID, notification)

        // A second "Start" click must not start a second loop
        if (timerJob == null) {
            timerJob = scope.launch {
                while (true) {
                    delay(1000)
                    _timerSeconds.update { it + 1 }
                    Log.d("Timer", "Минуло ${_timerSeconds.value} секунд")
                }
            }
        }
        return START_NOT_STICKY
    }

    // stopService() does not destroy a service while an Activity is bound to it,
    // so the screen also calls this method to stop the timer and remove the notification
    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerSeconds.value = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        Log.d("Timer", "Таймер зупинено")
    }

    override fun onDestroy() {
        scope.cancel() // stops the timer coroutine
        Log.d("Timer", "Service onDestroy")
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Таймер",
                NotificationManager.IMPORTANCE_LOW // no sound
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "timer_channel"
        private const val NOTIFICATION_ID = 1
    }
}
