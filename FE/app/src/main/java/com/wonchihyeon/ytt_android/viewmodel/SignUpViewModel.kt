package com.wonchihyeon.ytt_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wonchihyeon.ytt_android.data.model.user.Role
import com.wonchihyeon.ytt_android.data.model.user.SignUpDTO
import com.wonchihyeon.ytt_android.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)
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
        viewModelScope.launch {
            val signUpDTO = SignUpDTO(
                email.value ?: "",
                password.value ?: "",
                name.value ?: "",
                phoneNumber.value ?: "",
                Role.CUSTOMER.toString()
            )

            Log.d("SignUpDTO", signUpDTO.toString())

            val (response, _) = repository.signUp(signUpDTO)
            _signUpResponse.value = response
            Log.d("SignUpViewModel", "서버 응답: $response")

            if (response == "회원가입 성공") {
                _navigateToLogin.value = true
            }
        }
    }

    fun onNavigationComplete() {
        _navigateToLogin.value = false
    }
}
