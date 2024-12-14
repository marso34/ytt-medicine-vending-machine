package com.wonchihyeon.ytt_android.data.repository

import android.util.Log
import com.google.gson.Gson
import com.wonchihyeon.ytt_android.data.model.OrderDTO
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header

class VendingMachineRepository(private val apiService: ApiService) {

    fun getReissue(refreshToken: String): Call<ResponseDTO> {
        return apiService.getReissue(refreshToken)
    }

    fun addFavorites(machineId: Int): Call<ResponseDTO> {
       return apiService.addFavorites(machineId)
    }

    fun deleteFavorites(machineId: Int): Call<ResponseDTO> {
        return apiService.deleteFavorites(machineId)
    }

    fun getAllOrders(state: String?): Call<ResponseDTO> {
        return apiService.getAllOrders(state) // state를 매개변수로 전달
    }

    fun getFavorites(): Call<ResponseDTO> {
        return apiService.getFavorites()
    }

    fun Logout(): Call<ResponseDTO> {
        return apiService.Logout()
    }

    fun createOrder(orderDTO: OrderDTO): Call<ResponseDTO> {
        return apiService.createOrder(orderDTO)
    }

    fun cancelOrder(orderId: String): Call<ResponseDTO> {
        return apiService.cancelOrder(orderId)
    }

    fun myPage(): Call<ResponseDTO> {
        return apiService.getMyPage()
    }

    fun getVendingMachineById(vendingMachineId: String): Call<ResponseDTO> {
        return apiService.getVendingMachineById(vendingMachineId)
    }

    fun getNearByMachine(latitude: Double, longitude: Double, callback: (ResponseDTO) -> Unit) {
        apiService.getNearByMachine(latitude, longitude)
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
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

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.d("Network Failure", t.message ?: "Unknown error")
                    callback(ResponseDTO(400, "${t.message}", "error"))
                }
            })
    }
}
