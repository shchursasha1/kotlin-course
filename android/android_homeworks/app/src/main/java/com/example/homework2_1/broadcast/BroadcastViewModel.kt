package com.example.homework2_1.broadcast

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// AndroidViewModel gives the Application context for registerReceiver
class BroadcastViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BroadcastRepository(application)

    private val _events = MutableStateFlow<List<BroadcastEvent>>(emptyList())
    val events: StateFlow<List<BroadcastEvent>> = _events.asStateFlow()

    init {
        // The receiver lives as long as the ViewModel: it survives screen rotation.
        // In onCleared viewModelScope is cancelled, so awaitClose unregisters the receiver.
        viewModelScope.launch {
            repository.getEvents().collect { event ->
                // New events go to the top of the list
                _events.update { list -> listOf(event) + list }
            }
        }
    }

    fun deleteEvent(event: BroadcastEvent) {
        _events.update { list -> list.filter { it.id != event.id } }
    }

    fun clearAll() {
        _events.value = emptyList()
    }
}
