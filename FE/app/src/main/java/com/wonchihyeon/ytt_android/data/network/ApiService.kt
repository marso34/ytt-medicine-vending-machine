package com.wonchihyeon.ytt_android.data.network

import com.wonchihyeon.ytt_android.data.model.OrderDTO
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
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

    @POST("/user/password")

    @POST("/user/logout")

    @POST("/vending-machine/create")

    @POST("/vending-machine/add-medicine")

    @POST("/auth/reissue")*/

    // 즐겨찾기에삭제
    @DELETE("/favorites/{machineId}")
    fun deleteFavorites(@Path("machineId") machineId: Int): Call<ResponseDTO>

    // 즐겨찾기에추가
    @POST("/favorites/{machineId}")
    fun addFavorites(@Path("machineId") machineId: Int): Call<ResponseDTO>

    @GET("/orders")
    fun getAllOrders(@Query("state") state: String?): Call<ResponseDTO>

    @POST("/user/logout")
    fun Logout(): Call<ResponseDTO>



    // 주문처리결과
    @POST("/orders/store/{orderId}")
    fun resultOrder(
        @Body orderRequest: OrderDTO,
        @Query("orderId") orderId: String,
    ): Call<ResponseDTO>

    // 약 수령완료
    @POST("/orders/complete/{orderId}")
    fun completeOrder(
        @Body orderRequest: OrderDTO,
        @Query("orderId") orderId: String,
    ): Call<ResponseDTO>

    @GET("/user/mypage")
    fun getMyPage(): Call<ResponseDTO>

    // 주문생성
    @POST("/orders/create")
    fun createOrder(@Body orderRequest: OrderDTO): Call<ResponseDTO>

    @POST("/auth/reissue")
    fun refreshAccessToken(@Query("refresh") refreshToken: String): Call<ResponseDTO>

    // 주문처리결과
    @POST("/orders/store/{orderId}")
    fun orderResult(
        @Path("orderId") orderId: String,
    ): Call<ResponseDTO>

    // 주문취소
    @POST("/orders/cancel/{orderId}")
    fun cancelOrder(
        @Path("orderId") orderId: String,
    ): Call<ResponseDTO>

    // 즐겨찾기
    @GET("/favorites")
    fun getFavorites(): Call<ResponseDTO>

    @GET("/management")
    fun getManagementMachine(): Call<ResponseDTO>

    @GET("/management/{medicineId}")
    fun getManagement(): Call<ResponseDTO>

    @GET("/vending-machine/getFavorites")
    fun getFavoriteMachine(): Call<ResponseDTO>

    @GET("/vending-machine/nearby")
    fun getNearByMachine(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): Call<ResponseDTO>

    // 특정 자판기 ID로 조회 (상세조회)
    @GET("/vending-machine/{machineId}")
    fun getVendingMachineById(
        @Path("machineId") id: String,
    ): Call<ResponseDTO>

    // 특정 자판기의 전체 약 조회
    @GET("/vending-machine/{machineId}/medicines")
    fun getAllMedicineById(
        @Path("id") id: String,
    ): Call<ResponseDTO>

    @POST("/auth/reissue")
    fun getReissue(@Query("refresh") refreshToken:String): Call<ResponseDTO>

    // 특정 자판기의 특정 약 조회
    @GET("/vending-machine/{machineId}/medicine")
    fun getMedicineById(
        @Path("machineId") id: Int,
        @Query("id") medicineId: Int,
    ): Call<ResponseDTO>

    @POST("/user/signUp")
    fun signUp(@Body signUpDTO: SignUpDTO): Call<ResponseDTO>

    @POST("/user/signIn")
    fun signIn(@Body signInDTO: SignInDTO): Call<ResponseDTO>


}
