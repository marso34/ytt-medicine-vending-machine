package com.wonchihyeon.ytt_android.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.auth.LoginActivity
import com.wonchihyeon.ytt_android.data.TokenManager
import com.wonchihyeon.ytt_android.databinding.FragmentMyBinding

// 사용자 페이지
class MyFragment : Fragment() {
    private lateinit var binding: FragmentMyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my, container, false)

        // 로그아웃 버튼 클릭 리스너 설정
        binding.logoutButton.setOnClickListener {
            logout()
        }

        return binding.root
    }

    private fun logout() {
        // 토큰 삭제
        TokenManager.clearToken(context)


        startActivity(Intent(context, LoginActivity::class.java))
        requireActivity().finish() // 현재 프래그먼트 종료
    }
}
