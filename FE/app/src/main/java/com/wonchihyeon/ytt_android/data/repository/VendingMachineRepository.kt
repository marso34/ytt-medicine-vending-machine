package com.wonchihyeon.ytt_android.data.repository

import android.util.Log
import com.google.gson.Gson
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VendingMachineRepository(private val apiService: ApiService) {

    fun createOrder(): Call<ResponseDTO> {
        return apiService.createOrder()
    }

    fun myPage(): Call<ResponseDTO> {
        return apiService.createOrder()
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
