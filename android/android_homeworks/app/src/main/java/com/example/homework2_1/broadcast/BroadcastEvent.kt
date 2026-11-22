package com.example.homework2_1.broadcast

// One record in the system events log
data class BroadcastEvent(
    val id: String,        // unique key for LazyColumn
    val type: String,      // event name, shown in the collapsed card
    val timestamp: String, // time, shown in the collapsed card
    val details: String    // description, exact time and Intent extras
)
