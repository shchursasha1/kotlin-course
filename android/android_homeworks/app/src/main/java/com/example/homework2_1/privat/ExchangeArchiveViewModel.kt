package com.example.homework2_1.privat

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Screen states
sealed interface RatesUiState {
    // Before the first request
    data object Idle : RatesUiState
    data object Loading : RatesUiState
    // date - the date from the server answer
    data class Success(val date: String, val rates: List<ExchangeRate>) : RatesUiState
    // date - the date to retry with
    data class Error(val message: String, val date: String) : RatesUiState
}

class ExchangeArchiveViewModel : ViewModel() {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US).apply {
        // Reject dates like 31.02.2026 instead of turning them into 03.03.2026
        isLenient = false
    }

    // Selected date (text field value), survives rotation
    private val _dateText = MutableStateFlow(dateFormat.format(Date()))
    val dateText: StateFlow<String> = _dateText

    // Validation message under the date field, null = no error
    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError

    private val _uiState = MutableStateFlow<RatesUiState>(RatesUiState.Idle)
    val uiState: StateFlow<RatesUiState> = _uiState

    // Current request, so it can be cancelled
    private var currentCall: Call<ExchangeArchiveResponse>? = null

    fun onDateChange(text: String) {
        _dateText.value = text
        _dateError.value = null
    }

    // Retry after an error with the same date
    fun retry(date: String) {
        onDateChange(date)
        loadRates()
    }

    fun loadRates() {
        val date = _dateText.value.trim()
        val error = validateDate(date)
        if (error != null) {
            _dateError.value = error
            return
        }

        currentCall?.cancel()
        _uiState.value = RatesUiState.Loading
        Log.d(TAG, "Request start: date=$date")

        val call = PrivatBankNetworkModule.service.getExchangeArchive(date)
        currentCall = call
        call.enqueue(object : Callback<ExchangeArchiveResponse> {
            override fun onResponse(
                call: Call<ExchangeArchiveResponse>,
                response: Response<ExchangeArchiveResponse>
            ) {
                // Ignore old or cancelled calls
                if (call !== currentCall || call.isCanceled) return

                // HTTP errors (404, 500...) also come here, so check the status
                val body = response.body()
                if (!response.isSuccessful || body == null) {
                    Log.e(TAG, "HTTP error: ${response.code()}")
                    _uiState.value = RatesUiState.Error(
                        "Сервер повернув помилку (код ${response.code()}). Спробуйте знову",
                        date
                    )
                    return
                }

                // Items without a currency code are not shown
                val rates = body.exchangeRate.orEmpty()
                    .filter { !it.currency.isNullOrBlank() }
                Log.d(TAG, "Response: ${rates.size} currencies for ${body.date}")
                _uiState.value = RatesUiState.Success(body.date ?: date, rates)
            }

            override fun onFailure(call: Call<ExchangeArchiveResponse>, t: Throwable) {
                if (call !== currentCall || call.isCanceled) return
                // No internet, timeout or bad JSON
                Log.e(TAG, "Request failed", t)
                _uiState.value = RatesUiState.Error(
                    "Не вдалося завантажити курси. Перевірте з'єднання та спробуйте знову",
                    date
                )
            }
        })
    }

    // Returns an error text or null if the date is fine
    private fun validateDate(text: String): String? {
        if (!text.matches(Regex("""\d{2}\.\d{2}\.\d{4}"""))) {
            return "Введіть дату у форматі дд.мм.рррр"
        }
        val date = try {
            dateFormat.parse(text)
        } catch (e: ParseException) {
            null
        } ?: return "Такої дати не існує"

        val now = Date()
        if (date.after(now)) {
            return "Не можна обрати майбутню дату"
        }
        // The archive keeps only the last 4 years
        val minDate = Calendar.getInstance().apply { add(Calendar.YEAR, -4) }.time
        if (date.before(minDate)) {
            return "Архів зберігає курси лише за останні 4 роки"
        }
        return null
    }

    override fun onCleared() {
        super.onCleared()
        // Screen is closed for good: stop the unfinished request
        currentCall?.cancel()
    }

    companion object {
        const val TAG = "PrivatBank"
    }
}
