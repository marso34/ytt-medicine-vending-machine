package com.wonchihyeon.ytt_android


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.account.LoginActivity
import com.wonchihyeon.ytt_android.databinding.ActivityMainBinding
import com.wonchihyeon.ytt_android.fragments.FavoriteFragment
import com.wonchihyeon.ytt_android.fragments.HomeFragment
import com.wonchihyeon.ytt_android.fragments.MyFragment
import com.wonchihyeon.ytt_android.fragments.OrderFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var homeFragment: HomeFragment? = null // HomeFragment의 인스턴스를 저장할 변수

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000 // 요청 코드
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 위치 권한 확인
        checkLocationPermission()

        setBottomNavigationView()

        // 앱 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, homeFragment!!)
                .commit()
        }
    }

    @SuppressLint("ResourceType")
    fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_main -> {
                    // 홈 프래그먼트가 null이 아니면 재사용
                    if (homeFragment == null) {
                        homeFragment = HomeFragment()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, homeFragment!!)
                        .commit()
                    // 하단 다이얼로그 업데이트
                    homeFragment?.updateBottomSheet("등록된 자판기 목록")
                    true
                }
                R.id.fragment_favorite -> {
                    // 홈 프래그먼트의 인스턴스를 가져와서 하단 다이얼로그 업데이트
                    if (homeFragment == null) {
                        homeFragment = HomeFragment()
                    }
                    // 하단 다이얼로그 업데이트
                    homeFragment?.updateBottomSheet("즐겨찾기한 자판기 목록")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, homeFragment!!)
                        .commit()
                    true
                }
                R.id.fragment_order -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, OrderFragment())
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

    private fun checkLocationPermission() {
        // 위치 권한 체크
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 허용됨
                    // 위치 관련 작업 수행
                } else {
                    // 권한이 거부됨
                    // 로그인 화면으로 이동
                    navigateToLoginActivity()
                }
            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}
