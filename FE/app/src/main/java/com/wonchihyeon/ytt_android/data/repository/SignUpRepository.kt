package com.wonchihyeon.ytt_android.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpRepository(private val context: Context) {

    private val apiService: ApiService = RetrofitAPI.getAuthRetrofit(context).create(ApiService::class.java)

    fun signUp(signUpDTO: SignUpDTO, callback: (String, String?, String?) -> Unit) {
        apiService.signUp(signUpDTO).enqueue(object : Callback<ResponseDTO<String>> {
            override fun onResponse(
                call: Call<ResponseDTO<String>>,
                response: Response<ResponseDTO<String>>
            ) {
                Log.d("response_code", response.code().toString())
                if (response.isSuccessful) {
                    val responseBody : String = response.body().toString()
                    callback("회원가입 성공", responseBody, null)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "알 수 없는 오류"
                    callback("회원가입 실패: $errorMessage", null, null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                Log.e("SignUpRepository", "회원가입 요청 실패: ${t.message}", t)
                callback("회원가입 요청 실패: ${t.message}", null, null)
            }

        })
    }
}
