package com.wonchihyeon.ytt_android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.wonchihyeon.ytt_android.data.network.vendingmachineRetrofit
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import kotlinx.coroutines.Dispatchers

class VendingMachineViewModel : ViewModel() {
    private val repository = VendingMachineRepository(vendingmachineRetrofit.service)

    val vendingMachines = liveData(Dispatchers.IO) {
        val result = repository.fetchAllVendingMachines()
        emit(result)
    }
}
