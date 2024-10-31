package com.wonchihyeon.ytt_android.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.vendingmachine.vendingmachine
import com.wonchihyeon.ytt_android.databinding.FragmentHomeBinding
import com.wonchihyeon.ytt_android.viewmodel.VendingMachineViewModel
import java.io.IOException
import java.util.Locale

// 홈 페이지
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val vendingMachineViewModel: VendingMachineViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel = vendingMachineViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // ViewModel의 vendingMachines LiveData 관찰
        vendingMachineViewModel.vendingMachines.observe(viewLifecycleOwner) { result ->
            val vendingMachines = result.getOrNull() // 성공적인 경우 데이터 가져오기
            if (vendingMachines != null) {
                // 자판기 목록을 바텀시트에 표시
                binding.vendingMachineList.text = vendingMachines.joinToString("\n") { it.name }
            } else {
                binding.vendingMachineList.text = "자판기 목록을 불러오는 데 실패했습니다."
            }
        }

        return binding.root
    }
}
        private fun searchAddress(address: String) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            try {
                val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)

                // 마커 위치 설정 및 지도에 추가
                marker.position = latLng
                marker.map = naverMap

            } else {
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Naver Map 준비가 완료되면 호출되는 콜백
    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 지도 클릭 이벤트 처리
        naverMap.setOnMapClickListener { _, latLng ->
            handleMapClick(latLng)
        }

        // 마커 초기화
        marker = Marker()
    }

    // 지도 클릭 시 처리하는 함수
    private fun handleMapClick(latLng: LatLng) {
        // 마커 위치 설정 및 지도에 추가
        marker.position = latLng
        marker.map = naverMap

        // 주소 가져오기
        getAddressFromLatLng(latLng)
    }

    // 좌표를 주소로 변환하는 함수
    private fun getAddressFromLatLng(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressText = address.getAddressLine(0) ?: "주소를 찾을 수 없습니다."
            } else {
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    }
