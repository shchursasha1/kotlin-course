package com.example.homework2_1.flowcomparison

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigInteger
import kotlin.time.Duration.Companion.milliseconds

class CalculationViewModel : ViewModel() {

    // Experiment 1: cold Flow. To test it, uncomment this block,
    // comment out Experiment 2 below and add imports for Flow and flow.
    // It runs only while someone collects it and starts from 1 for every new collector.
    //
    // val fib: Flow<BigInteger> = flow {
    //     var f1 = BigInteger.ONE
    //     var f2 = BigInteger.ONE
    //     while (true) {
    //         Log.i("EEEE", "ViewModel: $f1")
    //         emit(f1)
    //         delay(500.milliseconds)
    //         val next = f1 + f2
    //         f1 = f2
    //         f2 = next
    //     }
    // }

    // Experiment 2: hot StateFlow. It is calculated in the ViewModel
    // all the time, even when nobody collects it.
    private val _fib = MutableStateFlow(BigInteger.ONE)
    val fib: StateFlow<BigInteger> = _fib.asStateFlow()

    init {
        viewModelScope.launch {
            var f1 = BigInteger.ONE
            var f2 = BigInteger.ONE
            while (true) {
                Log.i("EEEE", "ViewModel: $f1")
                _fib.emit(f1)
                delay(500.milliseconds)
                val next = f1 + f2
                f1 = f2
                f2 = next
            }
        }
    }
}
