package com.example.homework2_1

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.details)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Extras from `adb shell am start ... --es --ei --ez`
        val userName = intent.getStringExtra("user_name")
        val userId = intent.getIntExtra("user_id", 0)
        val isAdmin = intent.getBooleanExtra("is_admin", false)
        // Deep link url, null if the screen was opened without it
        val link = intent.data

        val text = "user_name: $userName\nuser_id: $userId\nis_admin: $isAdmin\nlink: $link"
        findViewById<TextView>(R.id.detailsText).text = text
        Log.d("MyTag", "DetailsActivity: $text")
    }
}
