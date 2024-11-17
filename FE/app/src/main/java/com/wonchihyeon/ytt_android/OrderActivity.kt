package com.wonchihyeon.ytt_android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.wonchihyeon.ytt_android.R

class OrderActivity : AppCompatActivity() {

    private lateinit var medicineNameTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var medicineImageView: ImageView
    private lateinit var orderButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        medicineNameTextView = findViewById(R.id.tv_medicine_name)
        priceTextView = findViewById(R.id.tv_price)
        medicineImageView = findViewById(R.id.iv_medicine_image)
        orderButton = findViewById(R.id.btn_order)

        // Intent로부터 데이터 받아오기
        val medicineName = intent.getStringExtra("medicineName")
        val price = intent.getStringExtra("price")
        val imageUrl = intent.getStringExtra("imageUrl")

        // UI에 데이터 설정
        medicineNameTextView.text = medicineName
        priceTextView.text = price
        Glide.with(this).load(imageUrl).into(medicineImageView)

        // 주문 버튼 클릭 시 행동
        orderButton.setOnClickListener {
            // 주문 처리 로직 구현
        }
    }
}
