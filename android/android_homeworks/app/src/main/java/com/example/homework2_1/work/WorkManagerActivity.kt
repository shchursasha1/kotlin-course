package com.example.homework2_1.work

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.homework2_1.R
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import java.util.concurrent.TimeUnit

class WorkManagerActivity : ComponentActivity() {

    // Statuses for Compose
    private val syncStatus = MutableStateFlow("Натисніть «Синхронізувати зараз»")
    private val nightStatus = MutableStateFlow("Нічна синхронізація: вимкнена")

    private var lastRequestId: UUID? = null
    private var syncLiveData: LiveData<WorkInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // After rotation: observe the same one-time request again
        lastRequestId = savedInstanceState?.getString(KEY_REQUEST_ID)?.let { UUID.fromString(it) }
        lastRequestId?.let { id -> observeSync(id) }

        // State of the periodic job (WorkManager keeps it even after app restart)
        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData(NIGHT_SYNC_NAME)
            .observe(this) { infos ->
                val info = infos.firstOrNull()
                nightStatus.value = if (info == null || info.state == WorkInfo.State.CANCELLED) {
                    "Нічна синхронізація: вимкнена"
                } else {
                    "Нічна синхронізація: увімкнена (${info.state.name})"
                }
            }

        setContent {
            val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            MaterialTheme(colorScheme = colors) {
                val status by syncStatus.collectAsStateWithLifecycle()
                val night by nightStatus.collectAsStateWithLifecycle()
                WorkManagerScreen(
                    syncStatus = status,
                    nightStatus = night,
                    onSyncNowClick = { syncNow() },
                    onNightSyncClick = { enableNightSync() }
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_REQUEST_ID, lastRequestId?.toString())
    }

    // One-time job: runs once when any internet is available
    private fun syncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<NewsSyncWorker>()
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString("user_token", "abc123").build())
            .build()

        WorkManager.getInstance(this).enqueue(request)

        lastRequestId = request.id
        observeSync(request.id)
    }

    private fun observeSync(id: UUID) {
        // Stop observing the previous request
        syncLiveData?.removeObservers(this)

        val liveData = WorkManager.getInstance(this).getWorkInfoByIdLiveData(id)
        syncLiveData = liveData
        liveData.observe(this) { workInfo ->
            if (workInfo != null) {
                syncStatus.value = when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> "Очікує... (ENQUEUED)"
                    WorkInfo.State.RUNNING -> "Виконується... (RUNNING)"
                    WorkInfo.State.SUCCEEDED -> {
                        val message = workInfo.outputData.getString("result")
                        "Успіх: $message"
                    }
                    else -> workInfo.state.name
                }
            }
        }
    }

    // Periodic job: only on Wi-Fi and while charging, so it does not drain the battery
    private fun enableNightSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()

        // 15 minutes is the minimum interval for periodic work
        val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString("user_token", "abc123").build())
            .build()

        // KEEP: if the job already exists, do not create a duplicate
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            NIGHT_SYNC_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    companion object {
        private const val NIGHT_SYNC_NAME = "night_news_sync"
        private const val KEY_REQUEST_ID = "last_request_id"
    }
}

@Composable
fun WorkManagerScreen(
    syncStatus: String,
    nightStatus: String,
    onSyncNowClick: () -> Unit,
    onNightSyncClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = syncStatus,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onSyncNowClick) {
                Text(stringResource(R.string.work_sync_now))
            }
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(onClick = onNightSyncClick) {
                Text(stringResource(R.string.work_night_sync))
            }
            Text(
                text = nightStatus,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
