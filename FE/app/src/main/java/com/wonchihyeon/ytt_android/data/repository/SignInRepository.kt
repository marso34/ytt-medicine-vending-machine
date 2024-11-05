package com.wonchihyeon.ytt_android.data.repository

import android.content.Context
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInRepository(private val context: Context) {

    private val apiService: ApiService = RetrofitAPI.getAuthRetrofit(context).create(ApiService::class.java)

    fun signIn(signInDTO: SignInDTO, callback: (String, String?) -> Unit) {
        apiService.signIn(signInDTO).enqueue(object : Callback<ResponseDTO<String>>  {
            override fun onResponse(
                call: Call<ResponseDTO<String>>,
                response: Response<ResponseDTO<String>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { token ->
                        callback("로그인 성공", token.toString())
                    } ?: run {
                        callback("로그인 실패: 응답이 없습니다", null)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "알 수 없는 오류"
                    callback("로그인 실패: $errorMessage", null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                callback("로그인 실패: ${t.message}", null)
            }
        })
    }
}