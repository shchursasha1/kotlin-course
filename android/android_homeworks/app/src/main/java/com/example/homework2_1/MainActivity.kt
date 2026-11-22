package com.example.homework2_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homework2_1.broadcast.BroadcastActivity
import com.example.homework2_1.coroutines.CoroutineErrorsActivity
import com.example.homework2_1.coroutines.DiceCoroutinesActivity
import com.example.homework2_1.flow.DownloadActivity
import com.example.homework2_1.flowcomparison.FlowComparisonActivity
import com.example.homework2_1.service.ServiceTimerActivity
import com.example.homework2_1.water.WaterReminderActivity
import com.example.homework2_1.work.WorkManagerActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var firstNumberInput: EditText
    private lateinit var secondNumberInput: EditText
    private lateinit var resultText: TextView

    private val audioPlayer = AudioPlayer()
    private val analyticsTracker = AnalyticsTracker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Timber creates the tag from the class name: MainActivity
        Timber.d("MainActivity успішно запущена!")
        Timber.d("MainActivity: onCreate() викликано")

        // Observers get lifecycle events by themselves
        lifecycle.addObserver(audioPlayer)
        lifecycle.addObserver(analyticsTracker)

        UserTracker.register(this)
        Timber.d("Зареєстровано слухачів у UserTracker: ${UserTracker.getListenersCount()}")

        firstNumberInput = findViewById(R.id.firstNumberInput)
        secondNumberInput = findViewById(R.id.secondNumberInput)
        resultText = findViewById(R.id.resultText)

        findViewById<Button>(R.id.plusButton).setOnClickListener { calculate("+") }
        findViewById<Button>(R.id.minusButton).setOnClickListener { calculate("-") }
        findViewById<Button>(R.id.multiplyButton).setOnClickListener { calculate("*") }
        findViewById<Button>(R.id.divideButton).setOnClickListener { calculate("/") }

        findViewById<Button>(R.id.clearButton).setOnClickListener {
            firstNumberInput.text.clear()
            secondNumberInput.text.clear()
            resultText.text = getString(R.string.result_empty)
            Log.d(TAG, "Clear")
        }

        findViewById<Button>(R.id.openFullCalculatorButton).setOnClickListener {
            startActivity(Intent(this, FullCalculatorActivity::class.java))
        }

        findViewById<Button>(R.id.countButton).setOnClickListener {
            val app = application as MyApp
            app.startCount++
            Timber.d("startCount = ${app.startCount}")
        }

        findViewById<Button>(R.id.openSecondButton).setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

        findViewById<Button>(R.id.openTimerButton).setOnClickListener {
            startActivity(Intent(this, TimerActivity::class.java))
        }

        findViewById<Button>(R.id.openDiceButton).setOnClickListener {
            startActivity(Intent(this, DiceActivity::class.java))
        }

        findViewById<Button>(R.id.openDiceCoroutinesButton).setOnClickListener {
            startActivity(Intent(this, DiceCoroutinesActivity::class.java))
        }

        findViewById<Button>(R.id.openCoroutineErrorsButton).setOnClickListener {
            startActivity(Intent(this, CoroutineErrorsActivity::class.java))
        }

        findViewById<Button>(R.id.openDownloadButton).setOnClickListener {
            startActivity(Intent(this, DownloadActivity::class.java))
        }

        findViewById<Button>(R.id.openFlowComparisonButton).setOnClickListener {
            startActivity(Intent(this, FlowComparisonActivity::class.java))
        }

        findViewById<Button>(R.id.openBroadcastLogButton).setOnClickListener {
            startActivity(Intent(this, BroadcastActivity::class.java))
        }

        findViewById<Button>(R.id.openServiceTimerButton).setOnClickListener {
            startActivity(Intent(this, ServiceTimerActivity::class.java))
        }

        findViewById<Button>(R.id.openWorkManagerButton).setOnClickListener {
            startActivity(Intent(this, WorkManagerActivity::class.java))
        }

        findViewById<Button>(R.id.openWaterReminderButton).setOnClickListener {
            startActivity(Intent(this, WaterReminderActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("MainActivity: onStart() викликано")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("MainActivity: onResume() викликано")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("MainActivity: onPause() викликано")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("MainActivity: onStop() викликано")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("MainActivity: onDestroy() викликано")
        // Fix for the memory leak: remove the destroyed Activity from the singleton
        UserTracker.unregister(this)
    }

    private fun calculate(operation: String) {
        val firstText = firstNumberInput.text.toString()
        val secondText = secondNumberInput.text.toString()

        if (firstText.isEmpty() || secondText.isEmpty()) {
            resultText.text = "Будь ласка, введіть обидва числа"
            Log.w(TAG, "Empty input for operation $operation")
            return
        }

        // null if the text is not a number, e.g. only "-" or "."
        val first = firstText.toDoubleOrNull()
        val second = secondText.toDoubleOrNull()
        if (first == null || second == null) {
            resultText.text = "Будь ласка, введіть коректні числа"
            Log.w(TAG, "Wrong input: $firstText, $secondText")
            return
        }

        Log.d(TAG, "Operation: $first $operation $second")

        if (operation == "/" && second == 0.0) {
            resultText.text = "Помилка: ділення на нуль неможливе!"
            Log.w(TAG, "Division by zero: $first / $second")
            return
        }

        val result = when (operation) {
            "+" -> first + second
            "-" -> first - second
            "*" -> first * second
            else -> first / second
        }

        resultText.text = "Результат: ${formatNumber(result)}"
        Log.d(TAG, "Result: $result")
    }

    // 5.0 -> "5", 5.5 -> "5.5"
    private fun formatNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            number.toLong().toString()
        } else {
            number.toString()
        }
    }

    companion object {
        private const val TAG = "Calculator"
    }
}
