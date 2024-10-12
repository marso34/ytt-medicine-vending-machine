package com.wonchihyeon.ytt_android

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // 전달된 주소 가져오기
        val address = intent.getStringExtra("ADDRESS")
        val addressTextView = findViewById<TextView>(R.id.addressDetailTextView)
        addressTextView.text = address ?: "주소 정보 없음"
    }
}