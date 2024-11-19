package com.wonchihyeon.ytt_android.data.repository

import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import retrofit2.Call

class MedicineRepository(private val apiService: ApiService) {
    fun getMedicineById(vendingMachineId: Int, medicineId: Int): Call<ResponseDTO> {
        return apiService.getMedicineById(vendingMachineId, medicineId)
    }

}
