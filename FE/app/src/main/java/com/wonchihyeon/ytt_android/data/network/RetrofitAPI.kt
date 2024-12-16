package com.wonchihyeon.ytt_android.data.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.wonchihyeon.ytt_android.BuildConfig
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
                    .addHeader("Authorization", "$token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }.build()

        var gson = GsonBuilder().setLenient().create()


        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getAuthRetrofit(context: Context): Retrofit {

        var gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}
