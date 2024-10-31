package com.wonchihyeon.ytt_android.data.network

import com.wonchihyeon.ytt_android.data.model.vendingmachine.VendingMachineResponse
import retrofit2.Response
import retrofit2.http.GET

interface VendingMachineService {
    @GET("/vending-machine/all")
    suspend fun getAllVendingMachines(): Response<VendingMachineResponse>
}