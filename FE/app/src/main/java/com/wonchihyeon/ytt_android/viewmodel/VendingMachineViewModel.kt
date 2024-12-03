package com.wonchihyeon.ytt_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDetailDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VendingMachineViewModel(application: Application) : AndroidViewModel(application) {

    val apiService = RetrofitAPI.getRetrofit(application).create(ApiService::class.java)

    private val repository
        get() = VendingMachineRepository(apiService)

    private val _vendingMachine = MutableLiveData<VendingMachineDetailDTO>()
    val vendingMachine: LiveData<VendingMachineDetailDTO> get() = _vendingMachine

    private val name = vendingMachine.value!!.name

    private val _orderedItems = MutableLiveData<List<MedicineDTO>>().apply {
        value = emptyList()
    }

    val orderedItems: LiveData<List<MedicineDTO>> get() = _orderedItems

    fun loadOrderedItemsFromPreferences() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "OrderPreferences",
            android.content.Context.MODE_PRIVATE
        )
        val allEntries = sharedPreferences.all

        val currentItems = mutableListOf<MedicineDTO>()
        val gson = Gson()

        for ((key, value) in allEntries) {
            if (value is String) {
                val item = gson.fromJson(value, MedicineDTO::class.java)
                currentItems.add(item)
            }
        }

        _orderedItems.value = currentItems // LiveData 값 업데이트
    }


    fun getOrderedItems(): List<MedicineDTO> {
        return _orderedItems.value ?: emptyList()
    }

    fun fetchMedicineDetails(vendingMachineId: String) {
        Log.d(
            "fetchMedicineDetailsVendingMachineDetail",
            "Fetching details for vendingMachineId: $vendingMachineId"
        )
        repository.getVendingMachineById(vendingMachineId).enqueue(object : Callback<ResponseDTO> {
            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                Log.d("VendingMachineDetail", "Response code: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        val body = response.body()!!.body as LinkedTreeMap<String, Any>
                        val gson = Gson()
                        val json = gson.toJson(body)
                        _vendingMachine.value =
                            gson.fromJson(json, VendingMachineDetailDTO::class.java)
                    } else {
                        Log.d("error", response.body()!!.message)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.d("error", "Error: ${t.message}")
            }
        })
    }
}

