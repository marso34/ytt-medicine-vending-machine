package com.wonchihyeon.ytt_android.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.databinding.FragmentHomeBinding
    import java.io.IOException
import java.util.Locale

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var naverMap: NaverMap
    private lateinit var marker: Marker
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // Naver Map 초기화
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_view, it).commit()
            }

        mapFragment.getMapAsync(this) // Naver Map 준비되면 콜백 실행

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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

        // 현재 위치로 가기 버튼 클릭 이벤트 설정
        binding.root.findViewById<Button>(R.id.current_location_button).setOnClickListener {
            moveToCurrentLocation()
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

                // 지도 위치 이동
                naverMap.moveCamera(com.naver.maps.map.CameraUpdate.scrollTo(latLng))
            } else {
                Log.d("HomeFragment", "주소를 찾을 수 없습니다.")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Naver Map 준비가 완료되면 호출되는 콜백
    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 마커 초기화
        marker = Marker()

        // 지정된 좌표에 마커 추가
        val preset1LatLng = LatLng(37.305121, 127.922653) // 강릉원주대 자판기 위치
        marker.position = preset1LatLng
        marker.map = naverMap
        marker.captionText = "강릉원주대 자판기" // 마커 이름 설정

        // 지정된 좌표에 마커 추가
        val preset2LatLng = LatLng(37.3025817, 127.9211587
        ) // 흥업면사무소 자판기 위치
        marker.position = preset2LatLng
        marker.map = naverMap
        marker.captionText = "흥업면사무소 자판기" // 마커 이름 설정

        // 마커를 추가한 후 지도 위치 이동
        naverMap.moveCamera(com.naver.maps.map.CameraUpdate.scrollTo(preset1LatLng))
        naverMap.moveCamera(com.naver.maps.map.CameraUpdate.scrollTo(preset2LatLng))

    }

    // 현재 위치로 이동하는 함수
    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)

                // 마커 위치 설정 및 지도에 추가
                marker.position = latLng
                marker.map = naverMap

                // 지도 위치 이동
                naverMap.moveCamera(com.naver.maps.map.CameraUpdate.scrollTo(latLng))
            } else {
                Log.d("HomeFragment", "현재 위치를 찾을 수 없습니다.")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                moveToCurrentLocation() // 권한이 허용되면 현재 위치로 이동
            }
        }
    }
}
