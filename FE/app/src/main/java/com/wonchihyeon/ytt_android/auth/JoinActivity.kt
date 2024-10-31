package com.wonchihyeon.ytt_android.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.databinding.ActivityJoinBinding
import com.wonchihyeon.ytt_android.viewmodel.SignUpViewModel
import com.wonchihyeon.ytt_android.auth.LoginActivity // 로그인 페이지로 이동하기 위한 import

class JoinActivity : AppCompatActivity() {

    private val viewModel by viewModels<SignUpViewModel>()
    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Data Binding 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // 회원가입 응답 관찰
        viewModel.signUpResponse.observe(this, Observer { response ->
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        })

        // 로그인 페이지로 이동 관찰
        viewModel.navigateToLogin.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate == true) {
                // 로그인 페이지로 이동
                startActivity(Intent(this, LoginActivity::class.java))
                viewModel.onNavigationComplete()  // 네비게이션 완료 처리
                finish() // 현재 Activity 종료 (선택 사항)
            }
        })
    }
}
