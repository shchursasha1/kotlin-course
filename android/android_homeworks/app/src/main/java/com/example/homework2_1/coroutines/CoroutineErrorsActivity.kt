package com.example.homework2_1.coroutines

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.homework2_1.databinding.ActivityCoroutineErrorsBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

// Practice: error handling and cancellation in coroutines
class CoroutineErrorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoroutineErrorsBinding

    private var calculationJob: Job? = null

    // Block 4: global handler for errors without try-catch
    private val ceh = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Глобальне перехоплення крашу: ${throwable.message}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCoroutineErrorsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Block 1: heavy work on a background thread
        binding.btnStart.setOnClickListener {
            calculationJob = lifecycleScope.launch(Dispatchers.Default) {
                try {
                    for (i in 1..100) {
                        Thread.sleep(50) // blocking work, it does not check cancellation
                        ensureActive()   // without this line cancel() does not stop the loop
                        Log.d(TAG, "Обробка: $i%")
                    }
                    Log.d(TAG, "Роботу успішно завершено!")
                } catch (e: CancellationException) {
                    Log.d(TAG, "Обробку скасовано користувачем!")
                    throw e // rethrow (do not swallow the error), so the coroutine really finishes as cancelled
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            calculationJob?.cancel()
        }

        // Blocks 2-3: in Block 2 there was no supervisorScope and no try-catch,
        // so the banner error cancelled the news feed and crashed the app
        binding.btnTestCrash.setOnClickListener {
            lifecycleScope.launch {
                supervisorScope {
                    // Child 1: banner
                    launch {
                        delay(1000)
                        try {
                            throw RuntimeException("Банер не завантажився!")
                        } catch (e: Exception) {
                            Log.e(TAG, "Не вдалося завантажити банер: ${e.message}")
                        }
                    }
                    // Child 2: news feed
                    launch {
                        delay(2000)
                        Log.d(TAG, "Стрічка новин завантажена")
                    }
                }
            }
        }

        // Block 4: no try-catch, the handler catches the error
        binding.btnGlobalCrash.setOnClickListener {
            val safeScope = CoroutineScope(SupervisorJob() + Dispatchers.Main + ceh)
            safeScope.launch {
                delay(500)
                throw IllegalArgumentException("Ой!")
            }
        }

        // Extra task: the app crashes, ceh is not called.
        // A child coroutine passes the error to its parent, so only the root's handler works.
        binding.btnNestedCrash.setOnClickListener {
            lifecycleScope.launch {
                launch(ceh) {
                    throw RuntimeException("Вкладений краш!")
                }
            }
        }
    }

    companion object {
        private const val TAG = "CoroutineErrors"
    }
}
