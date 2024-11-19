package com.wonchihyeon.ytt_android.data.network

import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDetailDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @POST("/user/logout")

    @POST("/vending-machine/create")

    @POST("/vending-machine/add-medicine")

    @POST("/auth/reissue")*/

    @GET("/vending-machine/nearby")
    fun getNearByMachine(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Call<ResponseDTO>

    // 특정 자판기 ID로 조회 (상세조회)
    @GET("/vending-machine/{id}")
    fun getVendingMachineById(
        @Path("id") id: String,
    ): Call<ResponseDTO>

    // 특정 자판기의 전체 약 조회
    @GET("/vending-machine/{id}/medicines")
    fun getAllMedicineById(
        @Path("id") id: String,
    ): Call<ResponseDTO>

    // 특정 자판기의 특정 약 조회
    @GET("/vending-machine/{id}/medicine")
    fun getMedicineById(
        @Path("id") id: Int,
        @Query("medicineId") medicineId: Int
    ): Call<ResponseDTO>

    @GET("/medicine/{id}")
    fun getMedicineById(
        @Path("medicineId") medicineId: Long
    ): Call<ResponseDTO>

    @POST("/user/signUp")
    fun signUp(@Body signUpDTO: SignUpDTO): Call<ResponseDTO>

    @POST("/user/signIn")
    fun signIn(@Body signInDTO: SignInDTO): Call<ResponseDTO>


}
