package com.wonchihyeon.ytt_android.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.auth.LoginActivity
import com.wonchihyeon.ytt_android.data.TokenManager
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.databinding.FragmentMyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 사용자 페이지
class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding
    private lateinit var repository: VendingMachineRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my, container, false)

        val apiService = RetrofitAPI.getRetrofit(this.requireContext()).create(ApiService::class.java)
        repository = VendingMachineRepository(apiService)

        binding.logoutButton.setOnClickListener {
            logout()
        }

        return binding.root
    }

    private fun logout() {
        val refreshToken = TokenManager.getRefreshToken(requireContext())

        if (refreshToken.isNullOrEmpty()) {
            Log.e("LogoutError", "Refresh token is empty.")
            // 사용자에게 알림 또는 에러 메시지 표시
            Toast.makeText(requireContext(), "로그아웃할 수 없습니다. 리프레시 토큰이 비어 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        repository.Logout(refreshToken).enqueue(object : Callback<ResponseDTO> {
            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("LogoutSuccess", "Logout successful: ${Gson().toJson(response.body())}")
                    startActivity(Intent(context, LoginActivity::class.java))
                    requireActivity().finish()
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.e("LogoutError", "Response not successful: ${response.code()}, Error: $errorResponse")
                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.e("LogoutError", "Error during logout: ${t.message}")
            }
        })
    }

}
