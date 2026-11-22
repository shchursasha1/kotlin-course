package com.example.homework2_1.water

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

// AndroidViewModel: needs the Application context for WorkManager and SharedPreferences
class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val prefs = context.getSharedPreferences(
        WaterReminderWorker.PREFS_NAME, Context.MODE_PRIVATE
    )

    // Switch and interval are saved too, so the screen looks the same after a restart
    private val _isEnabled = MutableStateFlow(prefs.getBoolean(KEY_ENABLED, false))
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _interval = MutableStateFlow(prefs.getString(KEY_INTERVAL, "15") ?: "15")
    val interval: StateFlow<String> = _interval.asStateFlow()

    private val _sentCount = MutableStateFlow(prefs.getInt(WaterReminderWorker.KEY_SENT_COUNT, 0))
    val sentCount: StateFlow<Int> = _sentCount.asStateFlow()

    // The Worker changes the counter in SharedPreferences, this listener shows it on the screen.
    // It is kept in a field: SharedPreferences holds listeners as weak references.
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == WaterReminderWorker.KEY_SENT_COUNT) {
            _sentCount.value = prefs.getInt(WaterReminderWorker.KEY_SENT_COUNT, 0)
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    fun onIntervalChange(text: String) {
        // Only digits
        _interval.value = text.filter { it.isDigit() }
    }

    fun startReminder(intervalMinutes: Long) {
        // The system minimum for periodic work is 15 minutes
        val safeInterval = if (intervalMinutes < 15) 15L else intervalMinutes

        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            safeInterval, TimeUnit.MINUTES
        ).build()

        // UPDATE: replaces the old settings of the job with the same name
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )

        _interval.value = safeInterval.toString()
        _isEnabled.value = true
        prefs.edit {
            putBoolean(KEY_ENABLED, true)
            putString(KEY_INTERVAL, safeInterval.toString())
        }
    }

    fun stopReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        _isEnabled.value = false
        prefs.edit { putBoolean(KEY_ENABLED, false) }
    }

    override fun onCleared() {
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
        super.onCleared()
    }

    companion object {
        private const val WORK_NAME = "water_reminder"
        private const val KEY_ENABLED = "reminder_enabled"
        private const val KEY_INTERVAL = "interval_minutes"
    }
}
