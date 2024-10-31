package com.wonchihyeon.ytt_android.data.repository

import android.content.Context
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInRepository(private val context: Context) {

    private val apiService: ApiService = RetrofitAPI.getRetrofit(context).create(ApiService::class.java)

    fun signIn(signInDTO: SignInDTO, callback: (String, String?) -> Unit) {
        apiService.signIn(signInDTO).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val token = response.body() // 응답으로 받은 토큰
                    callback("로그인 성공", token)
                } else {
                    callback("로그인 실패", null)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callback("로그인 실패", null)
            }
        })
    }
}
