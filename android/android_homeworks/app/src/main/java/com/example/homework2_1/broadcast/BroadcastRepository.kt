package com.example.homework2_1.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// Model: turns system broadcasts into a Flow of events
class BroadcastRepository(private val context: Context) {

    fun getEvents(): Flow<BroadcastEvent> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(createEvent(intent))
            }
        }

        // One receiver listens to several system events
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_HEADSET_PLUG)
        }

        // System broadcasts still come in, other apps cannot send to this receiver
        ContextCompat.registerReceiver(
            context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED
        )

        // Runs when the collector is cancelled: always unregister the receiver
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    private fun createEvent(intent: Intent): BroadcastEvent {
        val now = Date()
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now)
        val exactTime = SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS", Locale.getDefault()).format(now)

        val (type, description) = when (intent.action) {
            Intent.ACTION_POWER_CONNECTED ->
                "Зарядний пристрій підключено" to "Пристрій почав заряджатися."
            Intent.ACTION_POWER_DISCONNECTED ->
                "Зарядний пристрій відключено" to "Пристрій більше не заряджається."
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val isOn = intent.getBooleanExtra("state", false)
                "Режим «У літаку»: ${if (isOn) "увімкнено" else "вимкнено"}" to
                    "Змінився стан режиму «У літаку»."
            }
            Intent.ACTION_BATTERY_LOW ->
                "Низький рівень батареї" to "Заряд батареї став критично низьким."
            Intent.ACTION_BATTERY_OKAY ->
                "Рівень батареї в нормі" to "Батарея знову має достатній заряд."
            Intent.ACTION_HEADSET_PLUG -> {
                val isPlugged = intent.getIntExtra("state", 0) == 1
                "Навушники ${if (isPlugged) "підключено" else "відключено"}" to
                    "Змінився стан дротових навушників."
            }
            else -> (intent.action ?: "Невідома подія") to "Невідома системна подія."
        }

        val details = buildString {
            appendLine(description)
            appendLine("Action: ${intent.action}")
            appendLine("Точний час: $exactTime")
            append("Extras: ")
            val extras = intent.extras
            if (extras == null || extras.isEmpty) {
                append("немає")
            } else {
                for (key in extras.keySet()) {
                    @Suppress("DEPRECATION")
                    append("\n  $key = ${extras.get(key)}")
                }
            }
        }

        return BroadcastEvent(
            id = UUID.randomUUID().toString(),
            type = type,
            timestamp = time,
            details = details
        )
    }
}
