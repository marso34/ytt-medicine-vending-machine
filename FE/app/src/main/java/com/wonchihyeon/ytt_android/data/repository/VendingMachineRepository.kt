package com.wonchihyeon.ytt_android.data.repository

import android.util.Log
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VendingMachineRepository(private val apiService: ApiService) {

    // 모든 자판기 목록을 가져오는 메서드
    fun fetchAllVendingMachines(onResult: (List<VendingMachineDTO>?) -> Unit) {
        apiService.getAllVendingMachines().enqueue(object : Callback<List<VendingMachineDTO>> {
            override fun onResponse(
                call: Call<List<VendingMachineDTO>>,
                response: Response<List<VendingMachineDTO>>
            ) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    Log.e("VendingMachineRepo", "Error fetching vending machines: ${response.errorBody()?.string()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<List<VendingMachineDTO>>, t: Throwable) {
                Log.e("VendingMachineRepo", "Failure: ${t.message}")
                onResult(null)
            }
        })
    }

    // 특정 ID에 해당하는 자판기를 가져오는 메서드
    fun getVendingMachineById(id: String, callback: (VendingMachineDTO?) -> Unit) {
        apiService.getVendingMachineById(id).enqueue(object : Callback<VendingMachineDTO> {
            override fun onResponse(call: Call<VendingMachineDTO>, response: Response<VendingMachineDTO>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("VendingMachineRepo", "Error fetching vending machine by ID: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<VendingMachineDTO>, t: Throwable) {
                Log.e("VendingMachineRepo", "Failure fetching vending machine by ID: ${t.message}")
                callback(null)
            }
        })
    }
}
