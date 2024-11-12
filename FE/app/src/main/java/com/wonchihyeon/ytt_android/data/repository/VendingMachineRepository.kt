package com.wonchihyeon.ytt_android.data.repository

import android.util.Log
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VendingMachineRepository(private val apiService: ApiService) {

    fun getAllVendingMachines(callback: (ResponseDTO<List<VendingMachineDTO>>?) -> Unit) {
        apiService.getAllVendingMachines().enqueue(object : Callback<ResponseDTO<List<VendingMachineDTO>>> {
            override fun onResponse(
                call: Call<ResponseDTO<List<VendingMachineDTO>>>,
                response: Response<ResponseDTO<List<VendingMachineDTO>>>) {

                if (response.isSuccessful) {
                    Log.d("d", response.code().toString())
                    callback(response.body())
                } else {
                    Log.d("h", response.code().toString())
                    Log.d("d", response.body().toString())
                    Log.d("f", response.headers().toString())

                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseDTO<List<VendingMachineDTO>>>, t: Throwable) {
                Log.d("d", "실패")
                callback(null)
            }
        })
    }
}

private fun <T> Call<T>.enqueue(callback: Callback<ResponseDTO<List<VendingMachineDTO>>>) {
}
