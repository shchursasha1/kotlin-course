package com.example.homework2_1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Timber.d("SecondActivity: onCreate()")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("SecondActivity: onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("SecondActivity: onResume()")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("SecondActivity: onPause()")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("SecondActivity: onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("SecondActivity: onDestroy()")
    }
}
