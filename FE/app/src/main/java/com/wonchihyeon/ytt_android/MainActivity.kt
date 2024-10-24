package com.wonchihyeon.ytt_android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wonchihyeon.ytt_android.auth.LoginActivity
import com.wonchihyeon.ytt_android.databinding.ActivityMainBinding
import com.wonchihyeon.ytt_android.fragments.HomeFragment
import com.wonchihyeon.ytt_android.view.MyFragment
import com.wonchihyeon.ytt_android.view.OrderListFragment

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkLocationPermission() // 권한 확인 및 요청

        setBottomNavigationView()

        // 앱 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.fragment_main
        }
    }

    // 위치 권한 확인 및 요청
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // 권한이 있을 경우 HomeFragment 실행
            loadHomeFragment()
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 수락되었을 경우 HomeFragment 실행
                loadHomeFragment()
            } else {
                // 권한이 거절되었을 경우 LoginActivity로 이동
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // 현재 Activity 종료
            }
        }
    }

    // HomeFragment 로드 함수
    private fun loadHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()
    }

    @SuppressLint("ResourceType")
    fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_main -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, HomeFragment())
                        .commit()
                    true
                }

                R.id.fragment_favorite -> {
                    // 바텀 시트의 TextView 값을 "즐겨찾기"로 수정
                    findViewById<TextView>(R.id.address).text = "즐겨찾기"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, HomeFragment())
                        .commit()
                    true
                }

                R.id.fragment_order -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, OrderListFragment())
                        .commit()
                    true
                }

                R.id.fragment_my -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, MyFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}
