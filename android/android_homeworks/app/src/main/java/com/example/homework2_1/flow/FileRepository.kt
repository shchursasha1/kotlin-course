package com.example.homework2_1.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// Model: simulates a file download
class FileRepository {

    // Cold flow: the code inside starts only when someone calls collect()
    fun downloadFile(): Flow<Int> = flow {
        for (progress in 0..100 step 2) {
            emit(progress)
            delay(150) // network request simulation
        }
    }.flowOn(Dispatchers.IO) // the code above runs on a background IO thread
}
