package com.example.app.data.repository

import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpRepository {

    private val apiService: ApiService = RetrofitAPI.getRetrofit().create(ApiService::class.java)

    fun signUp(signUpDTO: SignUpDTO, callback: (String) -> Unit) {
        apiService.signUp(signUpDTO).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                // 응답 코드 확인
                val responseCode = response.code()
                if (response.isSuccessful) {
                    // 성공적인 응답
                    callback("회원가입 성공: ${response.body()} (응답 코드: $responseCode)")
                } else {
                    // 실패한 응답
                    callback("회원가입 실패: ${response.message()} (응답 코드: $responseCode)")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 요청 실패
                callback("회원가입 요청 실패: ${t.message}")
            }
        })
    }
}
