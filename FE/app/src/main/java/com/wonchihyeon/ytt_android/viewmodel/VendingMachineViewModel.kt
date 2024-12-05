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

    private val apiService = RetrofitAPI.getRetrofit(application).create(ApiService::class.java)
    private val repository = VendingMachineRepository(apiService)

    private val _vendingMachine = MutableLiveData<VendingMachineDetailDTO>()
    val vendingMachine: LiveData<VendingMachineDetailDTO> get() = _vendingMachine

   var boolean = _vendingMachine.value?.isFavorite

    val id = _vendingMachine.value?.id?.toInt()

    private val _orderedItems = MutableLiveData<List<MedicineDTO>>().apply {
        value = emptyList()
    }
    val orderedItems: LiveData<List<MedicineDTO>> get() = _orderedItems

    fun addOrderItem(item: MedicineDTO) {
        // 현재 주문 리스트를 가져옵니다.
        val currentList = _orderedItems.value?.toMutableList() ?: mutableListOf()

        // 새로운 아이템을 추가합니다.
        currentList.add(item)

        // SharedPreferences에 저장
        saveOrderToPreferences(item)

        // 업데이트된 리스트를 LiveData에 설정합니다.
        _orderedItems.value = currentList

        // 저장된 주문 항목 로그 출력
        logSavedOrders()
    }

    fun logSavedOrders() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("OrderPreferences", android.content.Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        Log.d("SavedOrders", "Currently saved orders:")
        for ((key, value) in allEntries) {
            if (key.startsWith("order_")) {
                val gson = Gson()
                val medicineItem = gson.fromJson(value as String, MedicineDTO::class.java)
                Log.d("SavedOrders", "Key: $key, ProductCode: ${medicineItem.productCode}, Quantity: ${medicineItem.stock}")

            }
        }
    }

    fun saveOrderToPreferences(item: MedicineDTO) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("OrderPreferences", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(item)

        // 아이템을 JSON 문자열로 변환하여 SharedPreferences에 저장
        editor.putString("order_${item.id}", json)
        editor.apply()
    }

    fun loadOrdersFromPreferences() {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("OrderPreferences", android.content.Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        val savedOrders = mutableListOf<MedicineDTO>()

        for ((key, value) in allEntries) {
            if (key.startsWith("order_")) {
                val gson = Gson()
                val medicineItem = gson.fromJson(value as String, MedicineDTO::class.java)
                savedOrders.add(medicineItem)
            }
        }

        // 업데이트된 주문 항목 리스트를 LiveData에 설정
        _orderedItems.value = savedOrders
    }

    fun getOrderedItems(): List<MedicineDTO> {
        return _orderedItems.value ?: emptyList()
    }

    fun fetchMedicineDetails(vendingMachineId: String) {
        Log.d("fetchMedicineDetails", "Fetching details for vendingMachineId: $vendingMachineId")
        repository.getVendingMachineById(vendingMachineId).enqueue(object : Callback<ResponseDTO> {
            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                Log.d("VendingMachineDetail", "Response code: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == 200) {
                        val body = response.body()!!.body as LinkedTreeMap<String, Any>
                        val gson = Gson()
                        val json = gson.toJson(body)
                        _vendingMachine.value = gson.fromJson(json, VendingMachineDetailDTO::class.java)
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

    fun getVendingMachineName(): String? {
        return _vendingMachine.value?.name
    }
}
