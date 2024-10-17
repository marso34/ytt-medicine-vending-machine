package com.wonchihyeon.ytt_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wonchihyeon.ytt_android.databinding.ActivityMainBinding
import com.wonchihyeon.ytt_android.fragments.FavoriteFragment
import com.wonchihyeon.ytt_android.fragments.HomeFragment
import com.wonchihyeon.ytt_android.fragments.MyFragment
import com.wonchihyeon.ytt_android.fragments.OrderListFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setBottomNavigationView()

        // 앱 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.fragment_main
        }
    }

    fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_main -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment()).commit()
                    true
                }
                R.id.fragment_favorite -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, FavoriteFragment()).commit()
                    true
                }
                R.id.fragment_order -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, OrderListFragment()).commit()
                    true
                }
                R.id.fragment_my -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_container, MyFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }
}