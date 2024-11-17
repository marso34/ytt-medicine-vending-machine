package com.wonchihyeon.ytt_android.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VendingMachineDetailActivity : AppCompatActivity() {

    private lateinit var repository: VendingMachineRepository
    private lateinit var medicineNameTextView: TextView
    private lateinit var manufacturerTextView: TextView
    private lateinit var efficacyTextView: TextView
    private lateinit var usageTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var stockTextView: TextView
    private lateinit var medicineImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending_machine_detail)

        // UI 요소 초기화
        medicineNameTextView = findViewById(R.id.tv_medicine_name)
        manufacturerTextView = findViewById(R.id.tv_manufacturer)
        efficacyTextView = findViewById(R.id.tv_efficacy)
        usageTextView = findViewById(R.id.tv_usage)
        priceTextView = findViewById(R.id.tv_price)
        stockTextView = findViewById(R.id.tv_stock)
        medicineImageView = findViewById(R.id.iv_medicine_image)

        // 레포지토리 및 API 서비스 초기화
        val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
        repository = VendingMachineRepository(apiService)

        // Intent로부터 자판기 ID와 약 ID 받아오기
        val vendingMachineId = intent.getStringExtra("vendingMachineId") ?: ""
        val medicineId = intent.getLongExtra("medicineId", 0L)

        Log.d("VendingMachineDetail", "vendingMachineId: $vendingMachineId, medicineId: $medicineId")

        // 상세 약 정보 불러오기
        fetchMedicineDetails(vendingMachineId, medicineId)
    }

    // 특정 약 상세 네
    private fun fetchMedicineDetails(vendingMachineId: String, medicineId: Long) {
        Log.d("VendingMachineDetail", "Fetching details for vendingMachineId: $vendingMachineId, medicineId: $medicineId")
        repository.getAllVendingMachineById(vendingMachineId).enqueue(object : Callback<ResponseDTO> {
            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                Log.d("VendingMachineDetail", "Response code: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    val medicineList = response.body()!!.body?.medicines
                    val medicine = medicineList?.find { it.id == medicineId }
                    if (medicine != null) {
                        Log.d("success", "Successfully loaded medicine details: $medicine")
                        displayMedicineDetails(medicine)
                    } else {
                        Log.d("else", "Medicine details are null.")
                    }
                } else {
                    Log.d("else if", "Failed to load medicine details.")
                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.d("error", "Error: ${t.message}")
            }
        })
    }

    private fun displayMedicineDetails(medicine: MedicineDTO) {
        Log.d("VendingMachineDetail", "Displaying medicine details: $medicine")
        medicineNameTextView.text = medicine.name
        manufacturerTextView.text = medicine.manufacturer ?: "제조사 정보 없음"
        efficacyTextView.text = medicine.efficacy ?: "효능 정보 없음"
        usageTextView.text = medicine.usage ?: "사용법 정보 없음"
        priceTextView.text = "가격: ${medicine.price} 원"
        stockTextView.text = "재고: ${medicine.stock}"

        // Glide를 사용하여 이미지 로드
        Glide.with(this).load(medicine.imageURL).into(medicineImageView)
    }
}
