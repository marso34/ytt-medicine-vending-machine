package com.wonchihyeon.ytt_android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDetailDTO
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
    private lateinit var orderedItemsListView: ListView
    private lateinit var orderedItemsAdapter: ArrayAdapter<String>
    private val orderedItems = mutableListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending_machine_detail)

        // 레포지토리 및 API 서비스 초기화
  r      val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
        repository = VendingMachineRepository(apiService)

        // Intent로부터 자판기 ID, 이름, 주소 받아오기
        val vendingMachineId = intent.getStringExtra("vendingMachineId") ?: ""
        val vendingMachineName = intent.getStringExtra("vendingMachineName") ?: ""
        val vendingMachineAddress = intent.getStringExtra("vendingMachineAddress") ?: ""

        findViewById<TextView>(R.id.vending_machine_name).text = vendingMachineName
        findViewById<TextView>(R.id.vending_machine_address).text = vendingMachineAddress

        // RecyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        orderedItemsListView = findViewById(R.id.ordered_items_list_view)
        orderedItemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, orderedItems)
        orderedItemsListView.adapter = orderedItemsAdapter

        // Intent로부터 전달된 데이터 받기
        val orderedItemsJson = intent.getStringExtra("orderedItems")
        orderedItemsJson?.let {
            val type = object : TypeToken<List<MedicineDTO>>() {}.type
            val items: List<MedicineDTO> = Gson().fromJson(it, type)
            populateOrderedItems(items)
        }

        // 상세 약 정보 불러오기
        fetchMedicineDetails(vendingMachineId)

        // 주문 버튼 설정
        setupOrderButton()

    }

    private fun setupOrderButton() {
        findViewById<Button>(R.id.order_button).setOnClickListener {
            // 주문 리스트를 JSON 형식으로 변환
            val orderedItemsJson = Gson().toJson(orderedItems)

            // OrderActivity로 이동
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("orderedItems", orderedItemsJson)
            startActivity(intent)
        }
    }


    private fun populateOrderedItems(items: List<MedicineDTO>) {
        // 기존 아이템을 유지한 채 새로운 아이템 추가
        for (item in items) {
            val totalPrice = item.price * item.stock
            // 같은 이름의 아이템이 이미 존재하지 않는지 체크
            if (!orderedItems.any { it.contains(item.name) }) {
                orderedItems.add("${item.name} - 수량: ${item.stock}, 총 가격: $totalPrice 원")
            }
        }
    }

    private fun fetchMedicineDetails(vendingMachineId: String) {
        Log.d("VendingMachineDetail", "Fetching details for vendingMachineId: $vendingMachineId")
        repository.getVendingMachineById(vendingMachineId).enqueue(object : Callback<ResponseDTO> {
            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                Log.d("VendingMachineDetail", "Response code: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.code == "200") {
                        val body = response.body()!!.body as LinkedTreeMap<String, Any>
                        val gson = Gson()
                        val json = gson.toJson(body)
                        val vendingMachine = gson.fromJson(json, VendingMachineDetailDTO::class.java)

                        adapter = MedicineAdapter(
                            vendingMachine.medicines,
                            this@VendingMachineDetailActivity,
                            vendingMachineId,
                            vendingMachine.name,
                            vendingMachine.address
                        )
                        recyclerView.adapter = adapter
                    } else {
                        Log.d("error", response.body()!!.message)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.d("error", "Error: ${t.message}")
            }
        })
    }
}
