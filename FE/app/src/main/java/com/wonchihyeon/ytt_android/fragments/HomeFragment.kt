package com.wonchihyeon.ytt_android.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.databinding.FragmentHomeBinding
import java.io.IOException
import java.util.Locale

// 홈 페이지
class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var naverMap: NaverMap
    private lateinit var marker: Marker
    private lateinit var behavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // Naver Map 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_view, it).commit()
            }

        mapFragment.getMapAsync(this) // Naver Map 준비되면 콜백 실행

        // 검색 바 클릭 이벤트 설정
        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            if (event?.action == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_SEARCH) {
                val address = binding.searchBar.text.toString()
                searchAddress(address)
                true
            } else {
                false
            }
        }
        initEvent()

        return binding.root
    }

    private fun initEvent() {
        persistentBottomSheetEvent()
    }

    private fun persistentBottomSheetEvent() {
        behavior = BottomSheetBehavior.from(binding.persistentBottomSheet)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d(TAG, "onStateChanged: 드래그 중")
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d(TAG, "onStateChanged: 접음")
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d(TAG, "onStateChanged: 드래그")
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d(TAG, "onStateChanged: 펼침")
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.d(TAG, "onStateChanged: 숨기기")
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        Log.d(TAG, "onStateChanged: 고정됨")
                    }
                }
            }
        })
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

                // 주소 텍스트 업데이트
                binding.address.text = location.getAddressLine(0)
            } else {
                binding.address.text = "주소를 찾을 수 없습니다."
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 지도 클릭 이벤트 처리
        naverMap.setOnMapClickListener { _, latLng ->
            handleMapClick(latLng)
        }

        // 마커 초기화
        marker = Marker()
    }

    private fun handleMapClick(latLng: LatLng) {
        // 마커 위치 설정 및 지도에 추가
        marker.position = latLng
        marker.map = naverMap

        // 클릭한 위치의 주소를 가져오기
        getAddressFromLatLng(latLng)
    }

    private fun getAddressFromLatLng(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                // 주소 텍스트 업데이트
                binding.address.text = address
            } else {
                binding.address.text = "주소를 찾을 수 없습니다."
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
