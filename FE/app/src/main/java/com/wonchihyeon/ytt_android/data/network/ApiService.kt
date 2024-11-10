package com.wonchihyeon.ytt_android.data.network

import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    /*@POST("/vending-machine/create")

    @POST("/vending-machine/add-medicine")

    @POST("/auth/reissue")*/

    @GET("/vending-machine/all")
    fun getAllVendingMachines(): Call<List<VendingMachineDTO>>

    @GET("/vending-machine/{id}")
    fun getVendingMachineById(@Path("id") id: String): Call<VendingMachineDTO>

    @POST("/user/signUp")
    fun signUp(@Body signUpDTO: SignUpDTO): Call<ResponseDTO<String>>

    @POST("/user/signIn")
    fun signIn(@Body signInDTO: SignInDTO): Call<ResponseDTO<String>>
}
