package com.wonchihyeon.ytt_android.fragments

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.wonchihyeon.ytt_android.OrderActivity
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.databinding.FragmentHomeBinding
import java.io.IOException
import java.util.Locale

// 홈 페이지
class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var naverMap: NaverMap
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

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


        binding.tipTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_tipFragment)
        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_talkFragment)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_bookmarkFragment)
        }

        binding.storeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_storeFragment)
        }

        return binding.root
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

                // 주소 하단 시트 표시
                showAddressInBottomSheet(location.getAddressLine(0) ?: "주소를 찾을 수 없습니다.")
            } else {
                showAddressInBottomSheet("주소를 찾을 수 없습니다.")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showAddressInBottomSheet("주소를 가져올 수 없습니다.")
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
                showAddressInBottomSheet(addressText) // 주소를 BottomSheet로 보여주기
            } else {
                showAddressInBottomSheet("주소를 찾을 수 없습니다.")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showAddressInBottomSheet("주소를 가져올 수 없습니다.")
        }
    }

    // 주소를 BottomSheet로 보여주는 함수
    private fun showAddressInBottomSheet(addressText: String) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_address, null)
        val addressTextView = bottomSheetView.findViewById<TextView>(R.id.addressTextView)
        addressTextView.text = addressText

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        // TextView 클릭 이벤트 설정 - 새로운 액티비티로 이동
        addressTextView.setOnClickListener {
            val intent = Intent(requireContext(), OrderActivity::class.java)
            intent.putExtra("ADDRESS", addressText)
            startActivity(intent)
            bottomSheetDialog.dismiss() // 클릭 후 바텀 시트 닫기
        }
    }
}