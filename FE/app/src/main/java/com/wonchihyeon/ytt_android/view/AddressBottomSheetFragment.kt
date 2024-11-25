package com.wonchihyeon.ytt_android.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.R.layout.bottom_sheet_address
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.ui.VendingMachineAdapter

class AddressBottomSheetFragment : Fragment(bottom_sheet_address) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VendingMachineAdapter
    private lateinit var repository: VendingMachineRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 레포지토리 초기화
        val apiService = RetrofitAPI.getRetrofit(requireContext()).create(ApiService::class.java)
        repository = VendingMachineRepository(apiService)

        // Nearby machines와 favorite machines를 가져옵니다.
        fetchNearByMachines(latitude = 37.305121, longitude = 127.922653)
        fetchFavorites()
    }

    private fun fetchFavorites() {
        Log.d("AddressBottomSheetFragment", "Fetching favorite vending machines...")
        repository.getFavorites { vendingMachineResponse ->
            if (vendingMachineResponse != null) {
                Log.d("AddressBottomSheetFragment", "Response Body: $vendingMachineResponse")

                if (vendingMachineResponse.code == "200") {
                    val list = vendingMachineResponse.body as List<LinkedTreeMap<String, Any>>

                    val gson = Gson()
                    val json = gson.toJson(list)
                    val listType = object : TypeToken<List<VendingMachineDTO>>() {}.type
                    val responseList: List<VendingMachineDTO> = gson.fromJson(json, listType)

                    responseList.forEach {
                        Log.d("favorite_list", it.toString())
                    }

                    // Adapter에 데이터를 설정
                    adapter = VendingMachineAdapter(responseList)
                    recyclerView.adapter = adapter
                } else {
                    Log.e("error", "${vendingMachineResponse.code}, ${vendingMachineResponse.message}")
                }
            } else {
                Log.d("AddressBottomSheetFragment", "Failed to fetch favorite vending machines.")
            }
        }
    }



    private fun fetchNearByMachines(latitude: Double, longitude: Double) {
        Log.d("AddressBottomSheetFragment", "Fetching nearby vending machines...")
        repository.getNearByMachine(latitude, longitude) { vendingMachineResponse ->
            if (vendingMachineResponse != null) {
                Log.d("AddressBottomSheetFragment", "Response Body: $vendingMachineResponse")

                if (vendingMachineResponse.code == "200") {
                    val list = vendingMachineResponse.body as List<LinkedTreeMap<String, Any>>

                    val gson = Gson()
                    val json = gson.toJson(list)
                    val listType = object : TypeToken<List<VendingMachineDTO>>() {}.type
                    val responseList: List<VendingMachineDTO> = gson.fromJson(json, listType)

                    responseList.forEach {
                        Log.d("list", it.toString())
                    }

                    // Adapter에 데이터를 설정
                    adapter = VendingMachineAdapter(responseList)
                    recyclerView.adapter = adapter
                } else {
                    // 200이 아니면 error 코드
                    Log.e(
                        "error",
                        "${vendingMachineResponse.code}, ${vendingMachineResponse.message}"
                    )
                }
            } else {
                Log.d("AddressBottomSheetFragment", "Failed to fetch nearby vending machines.")
            }
        }
    }
}
