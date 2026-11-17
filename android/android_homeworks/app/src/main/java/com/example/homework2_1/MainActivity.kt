package com.example.homework2_1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var firstNumberInput: EditText
    private lateinit var secondNumberInput: EditText
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Log for `adb logcat -s MyTag:D *:S`
        Log.d("MyTag", "MainActivity started")

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
