package com.wonchihyeon.ytt_android.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.wonchihyeon.ytt_android.MachineAdapter
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.databinding.FragmentHomeBinding

// 홈 페이지
class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var machineRepository: VendingMachineRepository
    private lateinit var naverMap: NaverMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        machineRepository = VendingMachineRepository(RetrofitAPI.getRetrofit(requireContext()).create(ApiService::class.java))

        // Naver Map 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this) // 지도 준비 완료 콜백 설정

        setupRecyclerView()
        loadVendingMachines() // 서버에서 자판기 데이터를 불러와 지도에 표시

        return binding.root
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MachineAdapter(emptyList()) { machineItem ->
                loadVendingMachineDetails(machineItem.id.toString())
            }
        }
    }

    // 자판기 목록을 불러와 RecyclerView 및 지도에 마커 추가
    private fun loadVendingMachines() {
        machineRepository.fetchAllVendingMachines { vendingMachines ->
            if (vendingMachines != null) {
                val machineItems = vendingMachines.map { it } // VendingMachineDTO 리스트
                (binding.recyclerView.adapter as MachineAdapter).updateData(machineItems) // 어댑터 데이터 업데이트

                // 자판기 정보를 로그로 출력
                for (machine in vendingMachines) {
                    Log.d("VendingMachineDTO", "ID: ${machine.id}, Name: ${machine.name}, Latitude: ${machine.latitude}, Longitude: ${machine.longitude}, Address: ${machine.address}")
                }

                // 지도에 마커 추가
                addMarkersToMap(vendingMachines)
            } else {
                Log.e("HomeFragment", "Failed to load vending machines.")
            }
        }
    }

    // 특정 자판기 정보를 가져오는 메서드
    private fun loadVendingMachineDetails(machineId: String) {
        machineRepository.getVendingMachineById(machineId) { vendingMachine ->
            vendingMachine?.let {
                Log.d("VendingMachineDetails", "ID: ${it.id}, Name: ${it.name}, Latitude: ${it.latitude}, Longitude: ${it.longitude}, Address: ${it.address}")
                // 추가적인 처리 (예: 상세 정보 화면으로 이동)
            } ?: run {
                Log.e("VendingMachineDetails", "Failed to load vending machine details.")
            }
        }
    }

    // Naver Map 준비 완료 시 호출되는 메서드
    override fun onMapReady(map: NaverMap) {
        naverMap = map
    }

    // 자판기 위치에 마커 추가
    private fun addMarkersToMap(vendingMachines: List<VendingMachineDTO>) {
        for (machine in vendingMachines) {
            val marker = Marker()
            marker.position = LatLng(machine.latitude, machine.longitude)
            marker.map = naverMap // Naver Map에 마커 표시

            // 지도 카메라를 첫 번째 자판기 위치로 이동
            if (vendingMachines.indexOf(machine) == 0) {
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(machine.latitude, machine.longitude)))
            }
        }
    }
}
