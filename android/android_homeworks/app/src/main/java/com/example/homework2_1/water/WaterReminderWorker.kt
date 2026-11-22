package com.example.homework2_1.water

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homework2_1.R

// Periodic job: counts the reminder and shows the "drink water" notification
class WaterReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Counter +1 in SharedPreferences (the Worker has no access to the ViewModel)
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_SENT_COUNT, 0) + 1
        prefs.edit { putInt(KEY_SENT_COUNT, count) }
        Log.d(TAG, "Reminder #$count")

        // 2. Notification, only if the user allowed it (Android 13+)
        if (hasNotificationPermission()) {
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
            createChannel(manager)

            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Трекер води")
                .setContentText("Час випити склянку води! 💧")
                .setSmallIcon(R.drawable.ic_water_drop)
                .setAutoCancel(true)
                .build()

            manager.notify(System.currentTimeMillis().toInt(), notification)
        } else {
            Log.d(TAG, "No POST_NOTIFICATIONS permission, notification skipped")
        }

        return Result.success()
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Нагадування про воду",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val PREFS_NAME = "water_prefs"
        const val KEY_SENT_COUNT = "reminders_sent"
        private const val CHANNEL_ID = "water_channel"
        private const val TAG = "WaterReminderWorker"
    }
}
