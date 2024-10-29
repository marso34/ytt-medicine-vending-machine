package com.wonchihyeon.ytt_android.data.repository

import com.wonchihyeon.ytt_android.data.model.vendingmachine.vendingmachine
import com.wonchihyeon.ytt_android.data.network.VendingMachineService

class VendingMachineRepository(private val service: VendingMachineService) {

    suspend fun fetchAllVendingMachines(): Result<List<vendingmachine>> {
        return try {
            val response = service.getAllVendingMachines()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.body)
            } else {
                Result.failure(Exception("Failed to load vending machines: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

