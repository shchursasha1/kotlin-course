package com.example.homework2_1

import android.os.CountDownTimer
import timber.log.Timber

// Model: only counts time, knows nothing about Activity, ViewModel or LiveData
class TimerModel {

    private var countDownTimer: CountDownTimer? = null

    fun start(seconds: Long, onTick: (Long) -> Unit, onFinish: () -> Unit) {
        // Cancel the old timer first, so two timers never run at the same time
        stop()

        countDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Round up: 29_990 ms -> 30 s
                val secondsLeft = (millisUntilFinished + 999) / 1000
                Timber.d("onTick: $secondsLeft")
                // .invoke() calls the callback parameter, not this onTick() method
                onTick.invoke(secondsLeft)
            }

            override fun onFinish() {
                Timber.d("onFinish")
                onFinish.invoke()
            }
        }.start()
    }

    fun stop() {
        countDownTimer?.cancel()
        countDownTimer = null
    }
}
