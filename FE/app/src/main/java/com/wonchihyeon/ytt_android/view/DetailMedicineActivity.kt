package com.wonchihyeon.ytt_android.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.MedicineRepository
import com.wonchihyeon.ytt_android.ui.VendingMachineDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailMedicineActivity : AppCompatActivity() {

    private lateinit var medicineNameTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var medicineImageView: ImageView
    private lateinit var orderButton: Button
    private lateinit var quantityTextView: TextView
    private lateinit var btnIncrease: Button
    private lateinit var btnDecrease: Button
    private lateinit var orderedItemsListView: ListView // ListView 선언
    private lateinit var repository: MedicineRepository
    private var vendingMachineId: String = ""
    private var medicineId: Int = 0
    private var quantity: Int = 1 // 초기 수량 설정

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_medicine)

        // UI 요소 초기화
        medicineNameTextView = findViewById(R.id.tv_medicine_name)
        priceTextView = findViewById(R.id.tv_price)
        medicineImageView = findViewById(R.id.iv_medicine_image)
        orderButton = findViewById(R.id.btn_order)
        quantityTextView = findViewById(R.id.tv_quantity)
        btnIncrease = findViewById(R.id.btn_increase)
        btnDecrease = findViewById(R.id.btn_decrease)

        // 레포지토리 및 API 서비스 초기화
        val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
        repository = MedicineRepository(apiService)

        // Intent로부터 약품 ID 받아오기
        medicineId = intent.getIntExtra("medicineId", 0)
        vendingMachineId = intent.getStringExtra("vendingMachineId").toString()

        // 약품 정보 불러오기
        fetchMedicineDetails(medicineId)

        // 수량 증가 버튼 클릭 시 행동
        btnIncrease.setOnClickListener {
            quantity++
            updateQuantityText()
        }

        // 수량 감소 버튼 클릭 시 행동
        btnDecrease.setOnClickListener {
            if (quantity > 1) { // 최소 수량 1
                quantity--
                updateQuantityText()
            }
        }

        // 주문 버튼 클릭 시 행동
        orderButton.setOnClickListener {
            addToOrderList()
            navigateToOrderActivity()
        }
    }

    // 주문 목록에 추가하는 함수
    private fun addToOrderList() {
        // MedicineDTO 생성
        val medicineDetail = MedicineDTO(
            id = medicineId.toLong(),
            name = medicineNameTextView.text.toString(),
            productCode = "",
            manufacturer = "",
            efficacy = "",
            usage = "",
            precautions = "",
            validityPeriod = "",
            price = priceTextView.text.toString().replace("가격: ", "").replace(" 원", "").toInt(),
            stock = quantity,
            imageURL = "",
            ingredients = emptyList() // 필요시 추가
        )

        // OrderActivity로 보내기 위한 데이터 저장
        val orderedItems = List(quantity) { medicineDetail } // 수량만큼 리스트 생성
        val intent = Intent(this, OrderActivity::class.java)
        intent.putExtra("orderedItems", Gson().toJson(orderedItems))
        startActivity(intent)
    }

    // OrderActivity로 이동하는 함수
    private fun navigateToOrderActivity() {
        // Intent는 addToOrderList에서 처리
    }

    private fun updateQuantityText() {
        quantityTextView.text = quantity.toString()
    }

    private fun fetchMedicineDetails(medicineId: Int) {
        val vendingMachineId = intent.getStringExtra("vendingMachineId")
        Log.d("API Request", "VendingMachineId: $vendingMachineId, MedicineId: $medicineId")

        vendingMachineId?.toInt()?.let {
            repository.getMedicineById(it, medicineId)
                .enqueue(object : Callback<ResponseDTO> {
                    override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                        if (response.isSuccessful && response.body() != null) {
                            Log.d("OrderActivity", "Response Body: ${Gson().toJson(response.body())}")

                            if (response.body()!!.code == "200") {
                                // 데이터 파싱
                                val body = response.body()!!.body as? LinkedTreeMap<*, *>
                                body?.let {
                                    val gson = Gson()
                                    val json = gson.toJson(body)
                                    val medicineDetail = gson.fromJson(json, MedicineDTO::class.java)

                                    // UI 업데이트
                                    medicineNameTextView.text = medicineDetail.name
                                    priceTextView.text = "가격: ${medicineDetail.price} 원"
                                    Glide.with(this@DetailMedicineActivity)
                                        .load(medicineDetail.imageURL)
                                        .into(medicineImageView)

                                    // 초기 수량을 서버에서 받아온 재고 수량으로 설정
                                    quantity = medicineDetail.stock
                                    updateQuantityText() // 수량 텍스트 업데이트

                                    // 추가 정보 반영
                                    findViewById<TextView>(R.id.tv_manufacturer).text = "제조사: ${medicineDetail.manufacturer}"
                                    findViewById<TextView>(R.id.tv_efficacy).text = "효능: ${medicineDetail.efficacy}"
                                    findViewById<TextView>(R.id.tv_usage).text = "사용법: ${medicineDetail.usage}"
                                    findViewById<TextView>(R.id.tv_precautions).text = "주의사항: ${medicineDetail.precautions}"
                                    findViewById<TextView>(R.id.tv_validity_period).text = "유효기간: ${medicineDetail.validityPeriod}"
                                }
                            } else {
                                Log.e("OrderActivity", response.body()!!.message)
                            }
                        } else {
                            Log.e("OrderActivity", "Response not successful: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                        Log.e("OrderActivity", "Error fetching medicine details: ${t.message}")
                    }
                })
        }
    }
}
