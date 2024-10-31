package com.wonchihyeon.ytt_android.data.repository

import android.content.Context
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.model.SignUpResponseDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpRepository(private val context: Context) {

    private val apiService: ApiService = RetrofitAPI.getRetrofit(context).create(ApiService::class.java)

    fun signUp(signUpDTO: SignUpDTO, callback: (String, String?, String?) -> Unit) {
        apiService.signUp(signUpDTO).enqueue(object : Callback<SignUpResponseDTO> {
            override fun onResponse(call: Call<SignUpResponseDTO>, response: Response<SignUpResponseDTO>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val accessToken = responseBody?.accessToken
                    val refreshToken = responseBody?.refreshToken
                    callback("회원가입 성공", accessToken, refreshToken)
                } else {
                    callback("회원가입 실패: ${response.message()}", null, null)
                }
            }

            override fun onFailure(call: Call<SignUpResponseDTO>, t: Throwable) {
                callback("회원가입 요청 실패: ${t.message}", null, null)
            }
        })
    }
}
