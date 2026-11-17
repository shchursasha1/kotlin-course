package com.example.homework2_1

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homework2_1.databinding.ActivityFullCalculatorBinding

class FullCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullCalculatorBinding

    // Calculator state
    private var firstNumber = 0.0
    private var operation = ""      // "" means no operation selected yet
    private var isNewNumber = true  // true: next digit starts a new number on the screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFullCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val digitButtons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3, binding.button4,
            binding.button5, binding.button6, binding.button7, binding.button8, binding.button9
        )
        for (button in digitButtons) {
            button.setOnClickListener { onDigit(button.text.toString()) }
        }

        binding.buttonPlus.setOnClickListener { onOperation("+") }
        binding.buttonMinus.setOnClickListener { onOperation("-") }
        binding.buttonMultiply.setOnClickListener { onOperation("*") }
        binding.buttonDivide.setOnClickListener { onOperation("/") }

        binding.buttonEquals.setOnClickListener { onEquals() }
        binding.buttonClear.setOnClickListener { onClear() }
        binding.buttonDot.setOnClickListener { onDot() }
        binding.buttonBackspace.setOnClickListener { onBackspace() }
    }

    private fun onDigit(digit: String) {
        val text = binding.display.text.toString()
        if (isNewNumber || text == "0" || text == ERROR) {
            binding.display.text = digit
            isNewNumber = false
        } else {
            binding.display.text = text + digit
        }
    }

    private fun onDot() {
        val text = binding.display.text.toString()
        if (isNewNumber || text == ERROR) {
            binding.display.text = "0."
            isNewNumber = false
        } else if (!text.contains(".")) {
            // Only one dot in a number
            binding.display.text = "$text."
        }
    }

    private fun onBackspace() {
        val text = binding.display.text.toString()
        // Don't edit a result or an error, only the number being typed
        if (isNewNumber || text == ERROR) return

        if (text.length == 1 || (text.length == 2 && text.startsWith("-"))) {
            binding.display.text = "0"
        } else {
            binding.display.text = text.dropLast(1)
        }
    }

    private fun onOperation(newOperation: String) {
        if (binding.display.text.toString() == ERROR) return

        // Chain: 2 + 3 + -> shows 5 and waits for the next number
        if (operation != "" && !isNewNumber) {
            if (!calculate()) return
        }

        firstNumber = binding.display.text.toString().toDouble()
        operation = newOperation
        isNewNumber = true
        Log.d(TAG, "First number: $firstNumber, operation: $operation")
    }

    private fun onEquals() {
        // Nothing to calculate yet, e.g. "2 =" or "2 + ="
        if (operation == "" || isNewNumber) return

        calculate()
        operation = ""
    }

    private fun onClear() {
        binding.display.text = "0"
        firstNumber = 0.0
        operation = ""
        isNewNumber = true
        Log.d(TAG, "Clear")
    }

    // Returns false if there was an error
    private fun calculate(): Boolean {
        val secondNumber = binding.display.text.toString().toDouble()

        if (operation == "/" && secondNumber == 0.0) {
            binding.display.text = ERROR
            Log.w(TAG, "Division by zero: $firstNumber / $secondNumber")
            firstNumber = 0.0
            operation = ""
            isNewNumber = true
            return false
        }

        val result = when (operation) {
            "+" -> firstNumber + secondNumber
            "-" -> firstNumber - secondNumber
            "*" -> firstNumber * secondNumber
            else -> firstNumber / secondNumber
        }

        Log.d(TAG, "$firstNumber $operation $secondNumber = $result")
        binding.display.text = formatNumber(result)
        isNewNumber = true
        return true
    }

    // 5.0 -> "5", 5.5 -> "5.5"
    private fun formatNumber(number: Double): String {
        return if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            number.toString()
        }
    }

    companion object {
        private const val TAG = "FullCalculator"
        private const val ERROR = "Помилка"
    }
}
