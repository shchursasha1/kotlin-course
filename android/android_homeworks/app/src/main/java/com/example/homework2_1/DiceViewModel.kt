package com.example.homework2_1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import kotlin.concurrent.thread
import kotlin.random.Random

class DiceViewModel : ViewModel() {

    private val _diceValues = MutableLiveData(List(DICE_COUNT) { 6 })
    val diceValues: LiveData<List<Int>> = _diceValues

    private val _isRolling = MutableLiveData(false)
    val isRolling: LiveData<Boolean> = _isRolling

    // Current values, changed by 5 background threads at the same time
    private val values = IntArray(DICE_COUNT) { 6 }

    // All running threads, to stop them in onCleared()
    private val threads = mutableListOf<Thread>()

    fun rollDice() {
        if (_isRolling.value == true) return

        // We are on the Main thread here, so setValue (.value) is fine
        _isRolling.value = true
        threads.clear()

        val diceThreads = mutableListOf<Thread>()
        for (index in 0 until DICE_COUNT) {
            // Each die rolls for its own random time: 1.5 - 3 seconds
            val steps = Random.nextInt(15, 31)
            diceThreads.add(thread(name = "Die-$index") { rollOneDie(index, steps) })
        }
        threads.addAll(diceThreads)

        // Waits until all 5 dice stop, then unlocks the ROLL button
        threads.add(thread(name = "Dice-waiter") {
            try {
                for (diceThread in diceThreads) {
                    diceThread.join()
                }
                Timber.d("All dice stopped: ${values.toList()}")
                // Background thread, so postValue instead of setValue
                _isRolling.postValue(false)
            } catch (e: InterruptedException) {
                Timber.d("Dice-waiter stopped")
            }
        })
    }

    private fun rollOneDie(index: Int, steps: Int) {
        try {
            repeat(steps) {
                // synchronized: only one thread changes and copies the array at a time
                synchronized(values) {
                    values[index] = Random.nextInt(1, 7)
                    _diceValues.postValue(values.toList())
                }
                Thread.sleep(100)
            }
            Timber.d("Die $index stopped after $steps steps")
        } catch (e: InterruptedException) {
            // Thread.sleep() throws it when the thread is interrupted in onCleared()
            Timber.d("Die $index interrupted")
        }
    }

    // Screen closed for good: stop all threads, so no zombie threads keep working
    override fun onCleared() {
        super.onCleared()
        for (thread in threads) {
            thread.interrupt()
        }
        Timber.d("DiceViewModel onCleared, threads stopped")
    }

    companion object {
        const val DICE_COUNT = 5
    }
}
