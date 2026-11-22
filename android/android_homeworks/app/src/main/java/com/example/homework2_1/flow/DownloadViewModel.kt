package com.example.homework2_1.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

// ViewModel: keeps the download state, survives screen rotation
class DownloadViewModel : ViewModel() {

    private val repository = FileRepository()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _status = MutableStateFlow("Натисніть «Скачати файл»")
    val status: StateFlow<String> = _status.asStateFlow()

    private var downloadJob: Job? = null

    fun startDownload() {
        // Do not start a second download while the first one is running
        if (downloadJob?.isActive == true) return

        downloadJob = viewModelScope.launch {
            repository.downloadFile()
                .onCompletion { cause ->
                    // cause == null: the flow finished normally, otherwise it was cancelled
                    _status.value = if (cause == null) {
                        "Файл успішно скачано!"
                    } else {
                        "Завантаження скасовано"
                    }
                }
                .collect { progress ->
                    _progress.value = progress
                    _status.value = "Завантаження: $progress%"
                }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
    }
}
