package com.example.homework2_1.privat

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PrivatBankApi {
    // GET https://api.privatbank.ua/p24api/exchange_rates?json&date=dd.MM.yyyy
    @GET("exchange_rates?json")
    fun getExchangeArchive(
        @Query("date") date: String
    ): Call<ExchangeArchiveResponse>
}

// One Retrofit instance for the whole app
object PrivatBankNetworkModule {
    // Base URL must end with "/"
    private const val BASE_URL = "https://api.privatbank.ua/p24api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: PrivatBankApi = retrofit.create(PrivatBankApi::class.java)
}
