package com.wonchihyeon.ytt_android.data.repository

import android.util.Log
import com.google.gson.Gson
import com.wonchihyeon.ytt_android.data.network.TokenManager
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.user.SignInDTO
import com.wonchihyeon.ytt_android.data.model.user.SignUpDTO
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

    fun getMyPage(
        callback: (ResponseDTO) -> Unit,
    ) {
        apiService.getMyPage()
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>,
                ) {
                    if (response.isSuccessful) {
                        Log.d("Response Success", response.code().toString())
                        response.body()?.let { callback(it) }
                    } else {
                        Log.d("Response Error", response.code().toString())
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                            callback(errorResponse)
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ResponseDTO>,
                    t: Throwable,
                ) {
                    Log.d("Network Failure", t.message ?: "Unknown error")
                    callback(ResponseDTO(400, "${t.message}", "error"))
                }
            })
    }



    suspend fun signIn(signInDTO: SignInDTO): Pair<String, Pair<String?, String?>> {
        return suspendCoroutine { continuation ->
            try {
                apiService.signIn(signInDTO).enqueue(object : Callback<ResponseDTO> {
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>,
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

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
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
                apiService.signUp(signUpDTO)
                    .enqueue(object : Callback<ResponseDTO> {
                     override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>,
                    ) {
                        if (response.isSuccessful) {
                            continuation.resume("회원가입 성공" to response.body()?.toString())
                        } else {
                            val errorMessage = response.errorBody()?.string()
                            errorMessage?.let {
                                val gson = Gson()
                                val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                                // 여기에 에러 로그 찍기
                                Log.d("Errorbody", "${errorResponse.code} ${errorResponse.message}")
                                continuation.resume("회원가입 실패" to errorResponse?.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        continuation.resume("회원가입 요청 실패: ${t.message}" to null)
                    }
                })
            } catch (e: Exception) {
                continuation.resume("회원가입 요청 실패: ${e.message}" to null)
            }
        }
    }

    suspend fun refreshAccessToken(refreshToken: String): String {
        return suspendCoroutine { continuation ->
            apiService.refreshAccessToken(refreshToken).enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if (response.isSuccessful) {
                        Log.d("refreshToken",response.code().toString())
                        val newAccessToken = response.headers()["Authorization"] // 새 액세스 토큰을 응답에서 가져옴
                        newAccessToken?.let {
                            TokenManager.saveAccessToken(context, it) // 새 액세스 토큰 저장
                            continuation.resume("토큰 재발급 성공")
                        } ?: continuation.resume("토큰 재발급 실패: 응답에 액세스 토큰이 없음")
                    } else {
                        continuation.resume("토큰 재발급 실패: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    continuation.resume("토큰 재발급 실패: ${t.message}")
                }
            })
        }
    }
}
