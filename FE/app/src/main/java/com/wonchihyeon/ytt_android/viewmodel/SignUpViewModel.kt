package com.wonchihyeon.ytt_android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.data.repository.SignUpRepository
import com.wonchihyeon.ytt_android.data.model.Role
import com.wonchihyeon.ytt_android.data.model.SignUpDTO

class SignUpViewModel : ViewModel() {

    private val repository = SignUpRepository()
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

        repository.signUp(signUpDTO) { response ->
            _signUpResponse.value = response

            // 서버 응답 처리
            if (response == "회원가입 성공") {  // 성공 메시지에 따라 수정
                _navigateToLogin.value = true
            }

            Log.d("SignUpViewModel", "서버 응답: $response")
        }

        Log.d("SignUpViewModel", "서버에 보내는 데이터: $signUpDTO")
    }

    fun onNavigationComplete() {
        _navigateToLogin.value = false
    }
}
