package com.wonchihyeon.ytt_android.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object vendingmachineRetrofit {
    private const val BASE_URL = "/vending-machine/all"

    val service: VendingMachineService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VendingMachineService::class.java)
    }
}