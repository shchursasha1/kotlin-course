package com.example.homework2_1.flowcomparison

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.homework2_1.databinding.ActivityFlowComparisonBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigInteger

// Flow (cold) vs StateFlow (hot): results are in Logcat, tag EEEE
class FlowComparisonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowComparisonBinding

    private val viewModel: CalculationViewModel by viewModels()

    private var fibonacciJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFlowComparisonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // by viewModels() is lazy: this line creates the ViewModel right away.
        // The same hashCode after rotation means the same ViewModel.
        Log.d("EEEE", "Activity created, ViewModel: ${viewModel.hashCode()}")

        // Click on "Підписатися"
        binding.btnSubscribe.setOnClickListener {
            if (fibonacciJob != null) {
                Toast.makeText(this, "Already subscribed", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fibonacciJob = lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.fib.collect { elem: BigInteger ->
                        Log.d("EEEE", "Activity : $elem")
                    }
                }
            }
        }

        // Click on "Скасувати"
        binding.btnCancel.setOnClickListener {
            fibonacciJob?.cancel()
            fibonacciJob = null
        }
    }
}
