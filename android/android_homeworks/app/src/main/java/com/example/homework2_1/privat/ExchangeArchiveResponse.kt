package com.example.homework2_1.privat

// Whole archive answer: an object with the "exchangeRate" array inside
// GET https://api.privatbank.ua/p24api/exchange_rates?json&date=01.11.2026
data class ExchangeArchiveResponse(
    val date: String?,
    val exchangeRate: List<ExchangeRate>?
)

// One item of "exchangeRate". Rates are JSON numbers, so Double is used.
// PrivatBank rates are optional: many currencies have only NBU rates.
data class ExchangeRate(
    val currency: String?,
    val baseCurrency: String?,
    val purchaseRate: Double?,
    val saleRate: Double?,
    val purchaseRateNB: Double?,
    val saleRateNB: Double?
)
