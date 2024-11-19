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
            val signInDTO = SignInDTO(
                email.value ?: "",
                password.value ?: ""
            )

            val (response, tokens) = repository.signIn(signInDTO)
            _signInResponse.value = response
            Log.d("w", response)

            tokens?.let { (token, refreshToken) ->
                if (response == "로그인 성공") {
                    if (token != null) {
                        TokenManager.saveAccessToken(getApplication(), token)
                    }
                    if (refreshToken != null) {
                        TokenManager.saveRefreshToken(getApplication(), refreshToken)
                    }
                    _navigateToHome.value = true
                }
            }

            Log.d("SignInViewModel", "서버 응답: $response")
        }
    }

    fun onNavigationComplete() {
        _navigateToHome.value = false
    }
}
