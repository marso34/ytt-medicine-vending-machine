package com.wonchihyeon.ytt_android.data.repository

import android.util.Log
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepository(private val context: android.content.Context) {
    private val apiService: ApiService =
        RetrofitAPI.getAuthRetrofit(context).create(ApiService::class.java)

    suspend fun signIn(signInDTO: SignInDTO): Pair<String, Pair<String?, String?>> {
        return suspendCoroutine { continuation ->
            try {
                apiService.signIn(signInDTO).enqueue(object : Callback<ResponseDTO<String>> {
                    override fun onResponse(
                        call: Call<ResponseDTO<String>>,
                        response: Response<ResponseDTO<String>>,
                    ) {
                        if (response.isSuccessful) {
                            val accessToken = response.headers()["Authorization"]
                            val refreshToken = response.headers()["refresh"]
                            continuation.resume("로그인 성공" to (accessToken to refreshToken))
                        } else {
                            val errorMessage = response.errorBody()?.string() ?: "알 수 없는 오류"
                            continuation.resume("로그인 실패: $errorMessage" to (null to null))
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                        continuation.resume("로그인 실패: ${t.message}" to (null to null))
                    }
                })
            } catch (e: Exception) {
                continuation.resume("로그인 실패: ${e.message}" to (null to null))
            }
        }
    }

    suspend fun signUp(signUpDTO: SignUpDTO): Pair<String, String?> {
        return suspendCoroutine { continuation ->
            try {
                apiService.signUp(signUpDTO).enqueue(object : Callback<ResponseDTO<String>> {
                    override fun onResponse(
                        call: Call<ResponseDTO<String>>,
                        response: Response<ResponseDTO<String>>,
                    ) {
                        if (response.isSuccessful) {
                            continuation.resume("회원가입 성공" to response.body()?.toString())
                        } else {
                            val errorMessage = response.errorBody()?.string() ?: "알 수 없는 오류"
                            continuation.resume("회원가입 실패: $errorMessage" to null)
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO<String>>, t: Throwable) {
                        continuation.resume("회원가입 요청 실패: ${t.message}" to null)
                    }
                })
            } catch (e: Exception) {
                continuation.resume("회원가입 요청 실패: ${e.message}" to null)
            }
        }
    }
}
