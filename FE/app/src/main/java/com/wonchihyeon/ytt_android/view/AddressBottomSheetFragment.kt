package com.wonchihyeon.ytt_android.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.R.layout.bottom_sheet_address
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

        view.findViewById<TextView>(R.id.pull).setOnClickListener {
            fetchNearByMachines(latitude = 37.305121, longitude = 127.922653) // 예시 좌표
        }
    }

    private fun fetchNearByMachines(latitude: Double, longitude: Double) {
        Log.d("AddressBottomSheetFragment", "Fetching nearby vending machines...")
        repository.getNearByMachine(latitude, longitude) { vendingMachineResponse ->
            if (vendingMachineResponse != null) {
                Log.d("AddressBottomSheetFragment", "Response Body: $vendingMachineResponse")

                // Adapter에 데이터를 설정
                adapter = VendingMachineAdapter(vendingMachineResponse.body ?: emptyList())
                recyclerView.adapter = adapter

                // 각 자판기 정보 로그 출력
                vendingMachineResponse.body?.forEach { machine ->
                    Log.d("AddressBottomSheetFragment", "Vending Machine:")
                    Log.d("AddressBottomSheetFragment", "  ID: ${machine.id}")
                    Log.d("AddressBottomSheetFragment", "  Name: ${machine.name}")
                    Log.d("AddressBottomSheetFragment", "  State: ${machine.state}")
                    Log.d("AddressBottomSheetFragment", "  Address: ${machine.address}")
                    Log.d("AddressBottomSheetFragment", "  Latitude: ${machine.latitude}")
                    Log.d("AddressBottomSheetFragment", "  Longitude: ${machine.longitude}")
                }
            } else {
                Log.d("AddressBottomSheetFragment", "Failed to fetch nearby vending machines.")
            }
        }
    }
}
