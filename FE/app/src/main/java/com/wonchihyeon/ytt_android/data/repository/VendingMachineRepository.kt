package com.wonchihyeon.ytt_android.data.repository

import android.util.Log
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VendingMachineRepository(private val apiService: ApiService) {


    fun getVendingMachineById(
        vendingMachineId: String,
        medicineId: Long,
    ): Call<ResponseDTO<MedicineDTO>> {
        return apiService.getVendingMachineById(vendingMachineId, medicineId)
    }

    fun getNearByMachine(
        latitude: Double,
        longitude: Double,
        callback: (ResponseDTO<List<VendingMachineDTO>>?) -> Unit,
    ) {
        apiService.getNearByMachine(latitude, longitude)
            .enqueue(object : Callback<ResponseDTO<List<VendingMachineDTO>>> {
                override fun onResponse(
                    call: Call<ResponseDTO<List<VendingMachineDTO>>>,
                    response: Response<ResponseDTO<List<VendingMachineDTO>>>,
                ) {
                    if (response.isSuccessful) {
                        Log.d("Response Success", response.code().toString())
                        callback(response.body())
                    } else {
                        Log.d("Response Error", response.code().toString())
                        callback(null)
                    }
                }

                override fun onFailure(
                    call: Call<ResponseDTO<List<VendingMachineDTO>>>,
                    t: Throwable,
                ) {
                    Log.d("Network Failure", t.message ?: "Unknown error")
                    callback(null)
                }
            })
    }
}
