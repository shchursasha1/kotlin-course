package com.example.homework2_1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homework2_1.databinding.ActivityTimerBinding

// View: only shows data and passes clicks to the ViewModel
class TimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding

    // Returns the same ViewModel after rotation, never create it with TimerViewModel()
    private val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.timerLiveData.observe(this) { seconds ->
            binding.tvTimer.text = seconds.toString()
        }

        viewModel.isTimerRunning.observe(this) { isRunning ->
            binding.btnStart.isEnabled = !isRunning
        }

        binding.btnStart.setOnClickListener {
            // null if the field is empty or not a number
            val userSeconds = binding.etSeconds.text.toString().toLongOrNull()
            viewModel.startTimer(userSeconds)
        }

        binding.btnReset.setOnClickListener {
            viewModel.resetTimer()
        }
    }
}
