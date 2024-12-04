package com.wonchihyeon.ytt_android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
import com.wonchihyeon.ytt_android.viewmodel.SignUpViewModel
import com.wonchihyeon.ytt_android.viewmodel.VendingMachineViewModel
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

    private val viewModel by viewModels<VendingMachineViewModel>()

    private lateinit var recyclerViewOrders: RecyclerView
    private lateinit var adapter2: OrdersAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending_machine_detail)

        // 레포지토리 및 API 서비스 초기화
        val apiService = RetrofitAPI.getRetrofit(this).create(ApiService::class.java)
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

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders)
        recyclerViewOrders.layoutManager = LinearLayoutManager(this)

        adapter2 = OrdersAdapter(emptyList())
        recyclerViewOrders.adapter = adapter2

        // 저장된 주문 항목 로드
        viewModel.loadOrdersFromPreferences()

        // ViewModel에서 데이터를 가져옴
        viewModel.orderedItems.observe(this) { items ->
            adapter2.updateData(items)
        }


        // Intent로부터 전달된 데이터 받기
        val orderedItemsJson = intent.getStringExtra("orderedItems")
        orderedItemsJson?.let {
            val type = object : TypeToken<List<MedicineDTO>>() {}.type
            val items: List<MedicineDTO> = Gson().fromJson(it, type)
            populateOrderedItems(items)
        }

        // 상세 약 정보 불러오기
        viewModel.fetchMedicineDetails(vendingMachineId)

        viewModel.vendingMachine.observe(this, Observer{
            Log.d("a", viewModel.vendingMachine.value!!.address)

            adapter = MedicineAdapter(
                viewModel.vendingMachine.value!!.medicines,
                this@VendingMachineDetailActivity,
                viewModel.vendingMachine.value!!.id.toString(),
                viewModel.vendingMachine.value!!.name.toString(),
                viewModel.vendingMachine.value!!.address.toString(),
            )
            recyclerView.adapter = adapter
        })

        // 주문 버튼 설정
        setupOrderButton(vendingMachineId)

    }

    private fun setupOrderButton(vendingMachineId: String) {
        findViewById<Button>(R.id.order_button).setOnClickListener {
            // 주문 리스트를 JSON 형식으로 변환
            val orderedItemsJson = Gson().toJson(orderedItems)

            // OrderActivity로 이동
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("orderedItems", orderedItemsJson)
            intent.putExtra("vendingMachineId",vendingMachineId)
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
}
