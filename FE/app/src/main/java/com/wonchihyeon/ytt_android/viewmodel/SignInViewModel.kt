package com.wonchihyeon.ytt_android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.repository.SignInRepository

class SignInViewModel : ViewModel() {

    private val repository = SignInRepository()
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

        repository.signIn(signInDTO) { response ->
            _signInResponse.value = response

            // 서버 응답 처리
            if (response == "로그인 성공") {  // 성공 메시지에 따라 수정
                _navigateToHome.value = true
            }

            Log.d("SignInViewModel", "서버 응답: $response")
        }

        Log.d("SignInViewModel", "서버에 보내는 데이터: $signInDTO")
    }

    fun onNavigationComplete() {
        _navigateToHome.value = false
    }
}
