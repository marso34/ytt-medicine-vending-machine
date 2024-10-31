package com.wonchihyeon.ytt_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wonchihyeon.ytt_android.data.TokenManager
import com.wonchihyeon.ytt_android.data.model.Role
import com.wonchihyeon.ytt_android.data.model.SignUpDTO
import com.wonchihyeon.ytt_android.data.repository.SignUpRepository

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SignUpRepository(application)
    private val _signUpResponse = MutableLiveData<String>()
    val signUpResponse: LiveData<String> get() = _signUpResponse
    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val role = MutableLiveData<Role>()

    fun signUp() {
        val signUpDTO = SignUpDTO(
            email.value ?: "",
            password.value ?: "",
            name.value ?: "",
            phoneNumber.value ?: "",
            Role.CUSTOMER
        )

        repository.signUp(signUpDTO) { response, accessToken, refreshToken ->
            _signUpResponse.value = response

            if (response == "회원가입 성공") {
                // 토큰 저장
                if (accessToken != null) {
                    TokenManager.saveAccessToken(getApplication(), accessToken)
                }
                if (refreshToken != null) {
                    TokenManager.saveRefreshToken(getApplication(), refreshToken)
                }

                _navigateToLogin.value = true // 로그인 화면으로 이동
            } else {
                _signUpResponse.value = "회원가입 실패"
            }

            Log.d("SignUpViewModel", "서버 응답: $response")
        }
    }

    fun onNavigationComplete() {
        _navigateToLogin.value = false
    }
}
