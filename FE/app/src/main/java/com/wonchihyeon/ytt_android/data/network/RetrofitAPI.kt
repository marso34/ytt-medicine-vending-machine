package com.wonchihyeon.ytt_android.data.network

import android.content.Context
import com.wonchihyeon.ytt_android.data.TokenManager
import com.wonchihyeon.ytt_android.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAPI {

    private const val BASE_URL = "${BuildConfig.BaseUrl}"

    fun getRetrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val token = TokenManager.getAccessToken(context)
            val request = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
