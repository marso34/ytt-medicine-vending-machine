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

        repository.signIn(signInDTO) { response, token ->
            _signInResponse.value = response
            // SignUpDTO 정보 로그 출력
                Log.d("w", response)


            if (response == "로그인 성공") {  // 성공 시 토큰 저장
                TokenManager.saveAccessToken(getApplication(), token!!)
                _navigateToHome.value = true
            }

            Log.d("SignInViewModel", "서버 응답: $response, 토큰: $token")
        }
    }

    fun onNavigationComplete() {
        _navigateToHome.value = false
    }
}
