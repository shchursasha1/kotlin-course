package com.example.homework2_1.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

// Background job: WorkManager runs it when the Constraints are met
class NewsSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // Runs in a background coroutine, not on the main thread
    override suspend fun doWork(): Result {
        val token = inputData.getString("user_token")
        Log.d(TAG, "Sync started, user_token = $token")

        delay(3000) // network request simulation

        val out = Data.Builder()
            .putString("result", "завантажено 15 новин")
            .build()
        Log.d(TAG, "Sync finished")
        return Result.success(out)
    }

    companion object {
        private const val TAG = "NewsSyncWorker"
    }
}
