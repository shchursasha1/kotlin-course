package com.example.homework2_1.flow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.homework2_1.databinding.ActivityDownloadBinding
import kotlinx.coroutines.launch

// View: only draws the state from the ViewModel
class DownloadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadBinding

    private val viewModel: DownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnDownload.setOnClickListener { viewModel.startDownload() }
        binding.btnCancel.setOnClickListener { viewModel.cancelDownload() }

        lifecycleScope.launch {
            // Collect only while the screen is visible, stop in the background
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.progress.collect { progress ->
                        binding.progressBar.progress = progress
                    }
                }
                launch {
                    viewModel.status.collect { status ->
                        binding.tvStatus.text = status
                    }
                }
            }
        }
    }
}
