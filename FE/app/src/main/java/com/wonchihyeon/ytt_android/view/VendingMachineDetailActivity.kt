package com.wonchihyeon.ytt_android.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.ui.adapter.MedicineAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VendingMachineDetailActivity : AppCompatActivity() {

    private lateinit var repository: VendingMachineRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicineAdapter
    private lateinit var medicineList: List<MedicineDTO>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending_machine_detail)

        // 레포지토리 및 API 서비스 초기화
        val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
        repository = VendingMachineRepository(apiService)

        // Intent로부터 자판기 ID 받아오기
        val vendingMachineId = intent.getStringExtra("vendingMachineId") ?: ""

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 상세 약 정보 불러오기
        fetchMedicineDetails(vendingMachineId)
    }

    private fun fetchMedicineDetails(vendingMachineId: String) {
        Log.d("VendingMachineDetail", "Fetching details for vendingMachineId: $vendingMachineId")
        repository.getVendingMachineById(vendingMachineId).enqueue(object : Callback<ResponseDTO> {
            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                Log.d("VendingMachineDetail", "Response code: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!.body
                    Log.d("VendingMachineDetail", "Response body: $body")

                    if (body is Map<*, *>) {
                        val medicinesJson = body["medicines"] as? List<*>
                        if (medicinesJson != null) {
                            val gson = Gson()
                            val json = gson.toJson(medicinesJson)
                            val listType = object : TypeToken<List<MedicineDTO>>() {}.type
                            medicineList = gson.fromJson(json, listType)

                            // Adapter에 데이터 설정, Context 전달
                            adapter = MedicineAdapter(medicineList, this@VendingMachineDetailActivity)
                            recyclerView.adapter = adapter
                        } else {
                            Log.d("error", "Medicines list is null")
                        }
                    } else {
                        Log.d("error", "Expected a Map but received: ${body?.javaClass}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.d("error", "Error: ${t.message}")
            }
        })
    }

}
