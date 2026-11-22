package com.example.homework2_1.service

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.homework2_1.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ServiceTimerActivity : ComponentActivity() {

    // Seconds from the service, passed to Compose
    private val timerFlow = MutableStateFlow(0)

    private var timerService: TimerForegroundService? = null
    private var isBound = false
    private var collectJob: Job? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerForegroundService.LocalBinder
            val myService = binder.getService()
            timerService = myService
            isBound = true

            // Connect the service flow to the Activity
            collectJob = lifecycleScope.launch {
                myService.getTimerFlow().collect { seconds ->
                    timerFlow.value = seconds
                }
            }
        }

        // Called only if the service process dies unexpectedly
        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            timerService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            MaterialTheme(colorScheme = colors) {
                val seconds by timerFlow.collectAsStateWithLifecycle()
                TimerServiceScreen(
                    seconds = seconds,
                    onStopClick = { context ->
                        val intent = Intent(context, TimerForegroundService::class.java)
                        timerService?.stopTimer()
                        context.stopService(intent)
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TimerForegroundService::class.java)
        // BIND_AUTO_CREATE: creates the service if it is not running yet
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            collectJob?.cancel()
            unbindService(connection)
            isBound = false
            timerService = null
        }
    }
}

@Composable
fun TimerServiceScreen(
    seconds: Int,
    onStopClick: (Context) -> Unit
) {
    val context = LocalContext.current

    fun startTimer() {
        val intent = Intent(context, TimerForegroundService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    // Asks for POST_NOTIFICATIONS, then starts the timer anyway:
    // without the permission the service works, only the notification is hidden
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        startTimer()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$seconds", fontSize = 96.sp)
            Text(text = stringResource(R.string.service_seconds))
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                if (needsPermission) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    startTimer()
                }
            }) {
                Text(stringResource(R.string.service_start))
            }
            OutlinedButton(onClick = { onStopClick(context) }) {
                Text(stringResource(R.string.service_stop))
            }
        }
    }
}
