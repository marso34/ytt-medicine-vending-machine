package com.wonchihyeon.ytt_android.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
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
import com.wonchihyeon.ytt_android.viewmodel.VendingMachineViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailMedicineActivity : AppCompatActivity() {

    private lateinit var medicineNameTextView: TextView
    private lateinit var medicineStockTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var medicineImageView: ImageView
    private lateinit var orderButton: Button
    private lateinit var quantityTextView: TextView
    private lateinit var btnIncrease: Button
    private lateinit var btnDecrease: Button
    private lateinit var orderedItemsListView: ListView // ListView 선언
    private lateinit var repository: MedicineRepository
    private var vendingMachineId: String = ""
    private var vendingMachineName: String = ""
    private var vendingMachineAddress: String = ""
    private var productCode: String = ""

    private var medicineId: Int = 0
    private var quantity: Int = 1 // 초기 수량 설정

    private val viewModel by viewModels<VendingMachineViewModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_medicine)

        // UI 요소 초기화
        medicineNameTextView = findViewById(R.id.detail_medicine_name)
        priceTextView = findViewById(R.id.detail_price)
        medicineImageView = findViewById(R.id.iv_medicine_image)
        orderButton = findViewById(R.id.btn_order)
        quantityTextView = findViewById(R.id.tv_quantity)
        btnIncrease = findViewById(R.id.btn_increase)
        btnDecrease = findViewById(R.id.btn_decrease)
        medicineStockTextView = findViewById(R.id.stock)

        // 레포지토리 및 API 서비스 초기화
        val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
        repository = MedicineRepository(apiService)

        // Intent로부터 약품 ID 받아오기

        medicineId = intent.getIntExtra("medicineId", 0)
        vendingMachineId = intent.getStringExtra("vendingMachineId").toString()
        vendingMachineName = intent.getStringExtra("vendingMachineName").toString()
        vendingMachineAddress = intent.getStringExtra("vendingMachineAddress").toString()
        productCode = intent.getStringExtra("productCode").toString()

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
            navigateToVendingMachineDetailActivity() // 수정된 부분
        }
    }

    private fun addToOrderList() {
        // MedicineDTO 생성
        val medicineDetail = MedicineDTO(
            id = medicineId.toLong(),
            name = medicineNameTextView.text.toString(),
            productCode = productCode, //필수
            manufacturer = "",
            efficacy = "",
            usage = "",
            precautions = "",
            validityPeriod = "",
            price = priceTextView.text.toString().replace("가격: ", "").replace(" 원", "").toInt(),
            stock = quantityTextView.text.toString().toInt(), // 필수 수량
            imageURL = "",
            ingredients = emptyList() // 필요 시 추가
        )

        // ViewModel에 주문 추가
        viewModel.addOrderItem(medicineDetail)

        // SharedPreferences에서 저장된 아이템 로드 및 로그 출력
        loadAndLogSavedOrder(medicineDetail.id)

        Toast.makeText(this, "주문이 추가되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun loadAndLogSavedOrder(medicineId: Long) {
        val sharedPreferences = getSharedPreferences("OrderPreferences", MODE_PRIVATE)
        val json = sharedPreferences.getString("order_$medicineId", null)

        if (json != null) {
            val gson = Gson()
            val medicineItem = gson.fromJson(json, MedicineDTO::class.java)
            Log.d("SavedMedicine", "Saved Medicine: $medicineItem")
        } else {
            Log.d("SavedMedicine", "No saved medicine found for id: $medicineId")
        }
    }



    private fun navigateToVendingMachineDetailActivity() {
        finish()
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
                    override fun onResponse(
                        call: Call<ResponseDTO>,
                        response: Response<ResponseDTO>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            Log.d(
                                "OrderActivity",
                                "Response Body: ${Gson().toJson(response.body())}"
                            )

                            if (response.body()!!.code == 200) {
                                // 데이터 파싱
                                val body = response.body()!!.body as? LinkedTreeMap<*, *>
                                body?.let {
                                    val gson = Gson()
                                    val json = gson.toJson(body)
                                    val medicineDetail =
                                        gson.fromJson(json, MedicineDTO::class.java)
                                    // UI 업데이트
                                    medicineNameTextView.text = medicineDetail.name
                                    priceTextView.text = "가격: ${medicineDetail.price} 원"
                                    if (!medicineDetail.imageURL.isEmpty()) {
                                        Glide.with(this@DetailMedicineActivity)
                                            .load(medicineDetail.imageURL)
                                            .into(medicineImageView)
                                    }
                                    medicineStockTextView.text = medicineDetail.stock.toString()
                                    // 초기 수량을 서버에서 받아온 재고 수량으로 설정
                                    quantity = 0
                                    updateQuantityText() // 수량 텍스트 업데이트

                                    // 추가 정보 반영
                                    findViewById<TextView>(R.id.tv_manufacturer).text =
                                        "제조사: ${medicineDetail.manufacturer}"
                                    findViewById<TextView>(R.id.tv_efficacy).text =
                                        "효능: ${medicineDetail.efficacy}"
                                    findViewById<TextView>(R.id.tv_usage).text =
                                        "사용법: ${medicineDetail.usage}"
                                    findViewById<TextView>(R.id.tv_precautions).text =
                                        "주의사항: ${medicineDetail.precautions}"
                                    findViewById<TextView>(R.id.tv_validity_period).text =
                                        "유효기간: ${medicineDetail.validityPeriod}"
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
