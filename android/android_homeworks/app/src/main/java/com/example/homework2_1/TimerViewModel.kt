package com.example.homework2_1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

// ViewModel: keeps the screen state and survives screen rotation
class TimerViewModel : ViewModel() {

    private val timerModel = TimerModel()

    // Only the ViewModel can change the value, the Activity can only observe it
    private val _timerLiveData = MutableLiveData(DEFAULT_SECONDS)
    val timerLiveData: LiveData<Long> = _timerLiveData

    private val _isTimerRunning = MutableLiveData(false)
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning

    init {
        Timber.d("TimerViewModel created")
    }

    // userSeconds is null if the input is empty or not a number
    fun startTimer(userSeconds: Long?) {
        val seconds = if (userSeconds == null || userSeconds <= 0) DEFAULT_SECONDS else userSeconds

        _timerLiveData.value = seconds
        _isTimerRunning.value = true

        timerModel.start(
            seconds,
            onTick = { secondsLeft -> _timerLiveData.value = secondsLeft },
            onFinish = {
                _timerLiveData.value = 0
                _isTimerRunning.value = false
            }
        )
    }

    fun resetTimer() {
        timerModel.stop()
        _timerLiveData.value = DEFAULT_SECONDS
        _isTimerRunning.value = false
    }

    // Called once, when the screen is closed for good (not on rotation)
    override fun onCleared() {
        super.onCleared()
        timerModel.stop()
        Timber.d("TimerViewModel onCleared, timer stopped")
    }

    companion object {
        const val DEFAULT_SECONDS = 30L
    }
}
