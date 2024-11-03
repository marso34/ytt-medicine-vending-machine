package com.wonchihyeon.ytt_android.data.network

import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    /*@GET("/medicine/{id}")

    @GET("/medicine/productCode")

    @GET("/medicine/name")

    @GET("/medicine/manufacturer")

    @GET("/medicine/ingredient")

    @GET("/medicine/all")

    @GET("/medicine/test")

    @GET("/user/mypage")

    @POST("/user/password")

    @POST("/user/logout")*/

  /*  @POST("/vending-machine/create")

    @POST("/vending-machine/add-medicine")

    @POST("/auth/reissue")

    @GET("/vending-machine/{id}")*/

    @GET("/vending-machine/nearby")
    fun getVendingMachineNearBy(@Body vendingMachineDTO: VendingMachineDTO): Call<String>
/*
    @GET("/vending-machine/name")

    @GET("/vending-machine/medicine")

    @GET("/vending-machine/all")*/


    @POST("/user/signUp")
    fun signUp(@Body signUpDTO: SignUpDTO): Call<String>


    @POST("/user/signIn")
    fun signIn(@Body signInDTO: SignInDTO): Call<String>
}
