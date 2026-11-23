package com.example.homework2_1.privat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.DecimalFormat

class PrivatBankActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // No request here: loading is started only by the button
        setContent {
            val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            MaterialTheme(colorScheme = colors) {
                PrivatBankScreen()
            }
        }
    }
}

@Composable
fun PrivatBankScreen(viewModel: ExchangeArchiveViewModel = viewModel()) {
    val dateText by viewModel.dateText.collectAsStateWithLifecycle()
    val dateError by viewModel.dateError.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is RatesUiState.Loading

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "ПриватБанк — валюти",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Date field and button are disabled while loading
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { viewModel.onDateChange(it) },
                    label = { Text("Дата (дд.мм.рррр)") },
                    isError = dateError != null,
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.loadRates() },
                    enabled = !isLoading
                ) {
                    Text("Показати курси")
                }
            }
            dateError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState) {
                RatesUiState.Idle -> CenterMessage("Оберіть дату та натисніть «Показати курси»")

                RatesUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is RatesUiState.Error -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.retry(state.date) }) {
                        Text("Повторити")
                    }
                }

                is RatesUiState.Success -> {
                    Text(
                        text = "Курси на ${state.date}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.rates.isEmpty()) {
                        CenterMessage("Немає даних за цю дату")
                    } else {
                        RatesList(state.rates)
                    }
                }
            }
        }
    }
}

// ⭐ Search and sorting work only with already loaded data, no new requests
@Composable
fun RatesList(rates: List<ExchangeRate>) {
    var query by rememberSaveable { mutableStateOf("") }
    var sortByName by rememberSaveable { mutableStateOf(false) }

    val shownRates = rates
        .filter { it.currency.orEmpty().contains(query.trim(), ignoreCase = true) }
        .let { list -> if (sortByName) list.sortedBy { it.currency } else list }

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Пошук за кодом") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        FilterChip(
            selected = sortByName,
            onClick = { sortByName = !sortByName },
            label = { Text("А→Я") }
        )
    }
    Spacer(modifier = Modifier.height(8.dp))

    if (shownRates.isEmpty()) {
        CenterMessage("Нічого не знайдено")
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shownRates, key = { it.currency.orEmpty() }) { rate ->
                RateCard(rate)
            }
        }
    }
}

@Composable
fun RateCard(rate: ExchangeRate) {
    val base = rate.baseCurrency ?: "UAH"
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rate.currency.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(72.dp)
            )
            Column {
                // Buy / sell from the bank's point of view
                Text("Банк купує: ${formatRate(rate.purchaseRate, base)}")
                Text("Банк продає: ${formatRate(rate.saleRate, base)}")
            }
        }
    }
}

// From 2 to 4 digits after the point: 50.30, 2.0835
private val rateFormat = DecimalFormat("0.00##")

// Missing PrivatBank rate -> "—", never 0 or the NBU rate
fun formatRate(value: Double?, base: String): String =
    if (value == null) "—" else "${rateFormat.format(value)} $base"

@Composable
fun CenterMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
