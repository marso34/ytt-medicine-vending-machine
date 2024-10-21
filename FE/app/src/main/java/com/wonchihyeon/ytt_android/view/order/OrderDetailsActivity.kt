package com.wonchihyeon.ytt_android.view.order

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wonchihyeon.ytt_android.OrderFailureActivity
import com.wonchihyeon.ytt_android.R

class OrderDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // 전달된 주소 가져오기
        val address = intent.getStringExtra("ADDRESS")
        val addressTextView = findViewById<TextView>(R.id.addressDetailTextView)
        addressTextView.text = address ?: "주소 정보 없음"

        findViewById<Button>(R.id.order_button).setOnClickListener{
            val intent = Intent(this, OrderFailureActivity::class.java)
            startActivity(intent)
        }
    }
}