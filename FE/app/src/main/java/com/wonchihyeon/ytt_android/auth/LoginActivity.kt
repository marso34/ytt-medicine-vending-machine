package com.wonchihyeon.ytt_android.auth

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.wonchihyeon.ytt_android.MainActivity
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.databinding.ActivityLoginBinding
import com.wonchihyeon.ytt_android.viewmodel.SignInViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<SignInViewModel>()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Data Binding 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

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
            }
        })

        // 회원가입 페이지로 이동
        binding.join.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }

        // 아이디 찾기 페이지로 이동
        binding.findId.setOnClickListener {
            val intent = Intent(this, FindIdActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 페이지로 이동
        binding.findPassword.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }

        getHashKey()
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
}
