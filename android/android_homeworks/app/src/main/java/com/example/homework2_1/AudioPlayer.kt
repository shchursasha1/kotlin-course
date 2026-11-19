package com.example.homework2_1

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

// Reacts to the screen lifecycle by itself, the Activity only calls addObserver()
class AudioPlayer : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        Timber.d("AudioPlayer: Відтворення фонового аудіо відновлено")
    }

    override fun onStop(owner: LifecycleOwner) {
        Timber.d("AudioPlayer: Відтворення фонового аудіо поставлено на паузу")
    }
}
