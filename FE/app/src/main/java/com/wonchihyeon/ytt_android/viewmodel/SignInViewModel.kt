package com.wonchihyeon.ytt_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wonchihyeon.ytt_android.data.TokenManager
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)
    private val _signInResponse = MutableLiveData<String>()
    val signInResponse: LiveData<String> get() = _signInResponse
    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> get() = _navigateToHome

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun signIn() {
        viewModelScope.launch {
            // 로그인 DTO 생성
            val signInDTO = SignInDTO(
                email.value ?: "",
                password.value ?: ""
            )

            // 로그인 요청
            val (response, tokens) = repository.signIn(signInDTO)
            _signInResponse.value = response
            Log.d("LoginResponse", response)

            // 로그인 성공 시 토큰 저장
            tokens?.let { (token, refreshToken) ->
                if (response == "로그인 성공") {
                    token?.let {
                        TokenManager.saveAccessToken(getApplication(), it) // 액세스 토큰 저장
                    }
                    refreshToken?.let {
                        TokenManager.saveRefreshToken(getApplication(), it) // 리프레시 토큰 저장
                    }
                    _navigateToHome.value = true // 홈으로 네비게이션
                }
            }

            Log.d("SignInViewModel", "서버 응답: $response")
        }
    }

    fun onNavigationComplete() {
        _navigateToHome.value = false
    }
}
