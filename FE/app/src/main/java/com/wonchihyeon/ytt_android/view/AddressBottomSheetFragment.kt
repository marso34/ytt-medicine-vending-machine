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
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
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

            fetchNearByMachines(latitude = 37.305121, longitude = 127.922653)

    }

    private fun fetchNearByMachines(latitude: Double, longitude: Double) {
        Log.d("AddressBottomSheetFragment", "Fetching nearby vending machines...")
        repository.getNearByMachine(latitude, longitude) { vendingMachineResponse ->
            if (vendingMachineResponse != null) {
                Log.d("AddressBottomSheetFragment", "Response Body: $vendingMachineResponse")

                if (vendingMachineResponse.code == "200") {

                    val List = vendingMachineResponse.body as List<LinkedTreeMap<String, Any>>

                    val gson = Gson()
                    val json = gson.toJson(List)
                    val listType = object : TypeToken<List<VendingMachineDTO>>() {}.type
                    val ResponseList: List<VendingMachineDTO> = gson.fromJson(json, listType)
                    ResponseList.forEach {
                        Log.d("list",it.toString())
                    }
                    //호출받으면 ui 할당
                    // Adapter에 데이터를 설정
                    adapter =
                        VendingMachineAdapter(ResponseList)
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
