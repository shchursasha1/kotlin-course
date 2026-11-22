package com.example.homework2_1.coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.random.Random

// Model: rolling logic only, knows nothing about Activity or LiveData
class DiceModel {

    // Changes the die value every 100 ms and returns the final value
    suspend fun rollDie(durationMs: Long, onValue: (Int) -> Unit): Int {
        var value = 1
        var time = 0L
        while (time < durationMs) {
            value = Random.nextInt(1, 7)
            onValue(value)
            // Suspends the coroutine, but does not block the thread
            delay(100)
            time += 100
        }
        return value
    }

    // Rolls all dice in parallel, returns when the last die stops
    suspend fun rollAll(count: Int, onValue: (index: Int, value: Int) -> Unit): List<Int> =
        coroutineScope {
            val dice = List(count) { index ->
                async {
                    // Each die rolls for its own random time: 1.5 - 3 seconds
                    val durationMs = Random.nextLong(1500, 3001)
                    rollDie(durationMs) { value -> onValue(index, value) }
                }
            }
            dice.awaitAll()
        }
}
