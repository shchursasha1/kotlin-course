package com.example.homework2_1

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

// Observes the whole app process (ProcessLifecycleOwner), not a single Activity
class BackgroundDetector : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        Timber.d("Застосунок перейшов у FOREGROUND (видимий на екрані)")
    }

    override fun onStop(owner: LifecycleOwner) {
        Timber.d("Застосунок перейшов у BACKGROUND (згорнутий у фон)")
    }
}
