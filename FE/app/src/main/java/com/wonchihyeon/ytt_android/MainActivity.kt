package com.wonchihyeon.ytt_android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.wonchihyeon.ytt_android.view.user.LoginActivity
import com.wonchihyeon.ytt_android.data.network.TokenManager
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.AuthRepository
import com.wonchihyeon.ytt_android.databinding.ActivityMainBinding
import com.wonchihyeon.ytt_android.fragments.HomeFragment
import com.wonchihyeon.ytt_android.view.FavoriteFragment
import com.wonchihyeon.ytt_android.view.MyFragment
import com.wonchihyeon.ytt_android.view.order.OrderListFragment
import kotlinx.coroutines.launch

/*import com.wonchihyeon.ytt_android.view.order.OrderListFragment*/

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var repository: AuthRepository

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

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        // 레포지토리 및 API 서비스 초기화
        val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
        repository = AuthRepository(this)

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
                    loadHomeFragment() // 홈 화면 로드
                    true
                }
                R.id.fragment_favorite -> {
                    // 즐겨찾기 탭 선택 시
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, FavoriteFragment())
                        .commit()
                    // FavoriteFragment에서 getFavorites()를 호출하도록 구현
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

    private fun checkAutoLogin() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {

            // 자동 로그인 처리
            startActivity(Intent(this, MainActivity::class.java))
            finish()

            val refreshToken = TokenManager.getRefreshToken(this)
            Log.d("autoLogin", refreshToken.toString())
            if (refreshToken.isNullOrEmpty()) {
                // 리프레시 토큰이 없으면 로그인 화면으로 이동
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }

            // CoroutineScope를 사용하여 리프레시 토큰으로 액세스 토큰 재발급 요청
            lifecycleScope.launch {
                val result = repository.refreshAccessToken(refreshToken)
                if (result.startsWith("토큰 재발급 성공")) {
                    // 토큰 재발급 성공 시 홈 페이지로 이동
                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                    finish()
                } else {
                    // 토큰 재발급 실패 시 로그인 페이지로 이동
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}

