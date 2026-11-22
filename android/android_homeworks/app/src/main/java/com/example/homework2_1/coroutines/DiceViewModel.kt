package com.example.homework2_1.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class DiceViewModel : ViewModel() {

    private val model = DiceModel()

    private val _diceValues = MutableLiveData(List(DICE_COUNT) { 6 })
    val diceValues: LiveData<List<Int>> = _diceValues

    private val _isRolling = MutableLiveData(false)
    val isRolling: LiveData<Boolean> = _isRolling

    private val _buttonText = MutableLiveData("ROLL")
    val buttonText: LiveData<String> = _buttonText

    private var job: Job? = null

    fun onRollClick() {
        // RESTART: stop the current rolling and start again from the beginning
        if (job?.isActive == true) {
            Timber.d("RESTART: cancel current rolling")
        }
        job?.cancel()

        // viewModelScope runs on the Main thread, so .value is safe here
        job = viewModelScope.launch {
            _isRolling.value = true
            _buttonText.value = "RESTART"

            val result = model.rollAll(DICE_COUNT) { index, value ->
                val values = _diceValues.value!!.toMutableList()
                values[index] = value
                _diceValues.value = values
            }

            // Runs only after all 5 dice stopped (not after cancel)
            Timber.d("All dice stopped: $result")
            _isRolling.value = false
            _buttonText.value = "ROLL"
        }
    }

    // viewModelScope cancels all coroutines by itself, here we only log it
    override fun onCleared() {
        super.onCleared()
        Timber.d("DiceViewModel (coroutines) onCleared")
    }

    companion object {
        const val DICE_COUNT = 5
    }
}
