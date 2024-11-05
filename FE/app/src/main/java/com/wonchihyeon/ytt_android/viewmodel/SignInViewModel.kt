package com.wonchihyeon.ytt_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wonchihyeon.ytt_android.data.TokenManager
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.repository.SignInRepository

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SignInRepository(application)  // Application context 전달
    private val _signInResponse = MutableLiveData<String>()
    val signInResponse: LiveData<String> get() = _signInResponse
    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> get() = _navigateToHome

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun signIn() {
        val signInDTO = SignInDTO(
            email.value ?: "",
            password.value ?: ""
        )

        repository.signIn(signInDTO) { response, token, refreshToken ->  // 세 개의 매개변수로 수정
            _signInResponse.value = response
            Log.d("w", response)

            if (response == "로그인 성공") {  // 성공 시 토큰 저장
                if (token != null) {
                    TokenManager.saveAccessToken(getApplication(), token)
                } else {
                    Log.e("SignInViewModel", "액세스 토큰이 null입니다.")
                }

                if (refreshToken != null) {
                    TokenManager.saveRefreshToken(getApplication(), refreshToken) // 리프레시 토큰 저장
                } else {
                    Log.e("SignInViewModel", "리프레시 토큰이 null입니다.")
                }

                _navigateToHome.value = true
            }

            Log.d("SignInViewModel", "서버 응답: $response, 액세스 토큰: $token, 리프레시 토큰: $refreshToken")
        }
    }

    fun onNavigationComplete() {
        _navigateToHome.value = false
    }
}
