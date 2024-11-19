package com.wonchihyeon.ytt_android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderActivity : AppCompatActivity() {

    private lateinit var medicineNameTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var medicineImageView: ImageView
    private lateinit var orderButton: Button
    private lateinit var repository: VendingMachineRepository

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        medicineNameTextView = findViewById(R.id.tv_medicine_name)
        priceTextView = findViewById(R.id.tv_price)
        medicineImageView = findViewById(R.id.iv_medicine_image)
        orderButton = findViewById(R.id.btn_order)

        // 레포지토리 및 API 서비스 초기화
        val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
        repository = VendingMachineRepository(apiService)

        // Intent로부터 약품 ID 받아오기
        val medicineId = intent.getLongExtra("medicineId", 0)

        // 약품 정보 불러오기
        fetchMedicineDetails(medicineId)

        // 주문 버튼 클릭 시 행동
        orderButton.setOnClickListener {
            // 주문 처리 로직 구현
        }
    }

    private fun fetchMedicineDetails(medicineId: Long) {
        val vendingMachineId = intent.getLongExtra("vendingMachineId", 0) // 자판기 ID를 Intent로부터 가져옴
        repository.getMedicineById(vendingMachineId,) { response ->
            if (response.code == "200") { // 성공적인 응답
                val body = response.body
                if (body is Map<*, *>) {
                    val gson = Gson()
                    val json = gson.toJson(body)
                    val medicineDetail = gson.fromJson(json, MedicineDTO::class.java)

                    // UI에 데이터 설정
                    medicineNameTextView.text = medicineDetail.name
                    priceTextView.text = "가격: ${medicineDetail.price} 원"
                    Glide.with(this).load(medicineDetail.imageURL).into(medicineImageView)

                    // 추가적인 데이터 표현 가능 (예: 성분 목록 등)
                    Log.d("OrderActivity", "약품 정보: $medicineDetail")
                }
            } else {
                Log.e("OrderActivity", "Error fetching medicine details: ${response.message}")
            }
        }
    }


}
