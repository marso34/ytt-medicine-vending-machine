package com.wonchihyeon.ytt_android.data.network

import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.model.SignUpResponseDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/user/signUp")
    fun signUp(@Body signUpDTO: SignUpDTO): Call<SignUpResponseDTO>


    @POST("/user/signIn")
    fun signIn(@Body signInDTO: SignInDTO): Call<String>
}
