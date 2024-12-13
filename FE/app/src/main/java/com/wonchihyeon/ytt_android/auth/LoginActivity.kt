package com.wonchihyeon.ytt_android.auth

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.wonchihyeon.ytt_android.MainActivity
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.TokenManager
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.AuthRepository
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.databinding.ActivityLoginBinding
import com.wonchihyeon.ytt_android.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<SignInViewModel>()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var repository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Data Binding 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)

        // 자동 로그인 체크
        checkAutoLogin()


        // 레포지토리 및 API 서비스 초기화
        val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
        repository = AuthRepository(this)


        // 로그인 응답 관찰
        viewModel.signInResponse.observe(this, Observer { response ->
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        })

        // 홈 프래그먼트로 이동 관찰
        viewModel.navigateToHome.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate == true) {
                // 홈 페이지로 이동
                startActivity(Intent(this, MainActivity::class.java))
                viewModel.onNavigationComplete() // 네비게이션 완료 처리
                finish() // 현재 Activity 종료 (선택 사항)

                // 로그인 유지 설정
                saveLoginInfo()
            }
        })

        // 회원가입 페이지로 이동
        binding.join.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }

        // 아이디 찾기 페이지로 이동
        binding.findId.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 비밀번호 찾기 페이지로 이동
        binding.findPassword.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }
        checkAutoLogin()
        getHashKey()
    }

    private fun saveLoginInfo() {
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", binding.checkboxRememberMe.isChecked) // 체크박스 상태 저장
            apply()
        }
    }

    private fun getHashKey() {
        // 해시 키를 가져오는 코드 (변경 없음)
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
    }

    private fun checkAutoLogin() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            // 자동 로그인 처리
            startActivity(Intent(this, MainActivity::class.java))
            finish()

            val refreshToken = TokenManager.getRefreshToken(this)
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
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // 토큰 재발급 실패 시 로그인 페이지로 이동
                    startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}
