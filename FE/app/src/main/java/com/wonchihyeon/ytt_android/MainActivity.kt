package com.wonchihyeon.ytt_android

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wonchihyeon.ytt_android.databinding.ActivityMainBinding
import com.wonchihyeon.ytt_android.fragments.FavoriteFragment
import com.wonchihyeon.ytt_android.fragments.HomeFragment
import com.wonchihyeon.ytt_android.fragments.MyFragment
import com.wonchihyeon.ytt_android.fragments.OrderFragment

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var homeFragment: HomeFragment? = null // HomeFragment의 인스턴스를 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setBottomNavigationView()

        // 앱 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, homeFragment!!)
                .commit()
        }
    }

    @SuppressLint("ResourceType")
    fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_main -> {
                    // 홈 프래그먼트가 null이 아니면 재사용
                    if (homeFragment == null) {
                        homeFragment = HomeFragment()
                    }
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, homeFragment!!)
                        .commit()
                    // 하단 다이얼로그 업데이트
                    homeFragment?.updateBottomSheet("홈 화면입니다.")
                    true
                }
                R.id.fragment_favorite -> {
                    // 홈 프래그먼트의 인스턴스를 가져와서 하단 다이얼로그 업데이트
                    if (homeFragment == null) {
                        homeFragment = HomeFragment()
                    }
                    // 하단 다이얼로그 업데이트
                    homeFragment?.updateBottomSheet("즐겨찾기에서 돌아왔습니다.")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, homeFragment!!)
                        .commit()
                    true
                }
                R.id.fragment_order -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, OrderFragment())
                        .commit()
                    true
                }
                R.id.fragment_my -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, MyFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}