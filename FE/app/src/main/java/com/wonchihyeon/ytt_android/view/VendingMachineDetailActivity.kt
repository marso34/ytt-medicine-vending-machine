package com.wonchihyeon.ytt_android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.SignInDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.ui.adapter.MedicineAdapter
import com.wonchihyeon.ytt_android.ui.adapter.OrdersAdapter
import com.wonchihyeon.ytt_android.viewmodel.VendingMachineViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VendingMachineDetailActivity : AppCompatActivity() {

    private lateinit var repository: VendingMachineRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicineAdapter
    private lateinit var orderedItemsListView: ListView
    private lateinit var orderedItemsAdapter: ArrayAdapter<String>
    private val orderedItems = mutableListOf<String>()

    private lateinit var adapter1: VendingMachineAdapter


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

        adapter2 = OrdersAdapter(mutableListOf())
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

        viewModel.vendingMachine.observe(this, Observer {
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

        // SharedPreferences 초기화
        val sharedPreferences = getSharedPreferences("FavoritePreferences", MODE_PRIVATE)

        // SharedPreferences에서 boolean 상태 로드
        viewModel.boolean = sharedPreferences.getBoolean(
            "isFavorite_${intent.getStringExtra("vendingMachineId")}",
            false
        )

        // 초기 UI 상태 설정
        if (viewModel.boolean!!) {
            findViewById<ImageView>(R.id.heart_image_view).setImageResource(R.drawable.ic_heart_fill)
        } else {
            findViewById<ImageView>(R.id.heart_image_view).setImageResource(R.drawable.ic_heart)
        }

        // heart_image_view 클릭하면 즐겨찾기 추가/삭제
        findViewById<ImageView>(R.id.heart_image_view).setOnClickListener {
            val editor = sharedPreferences.edit() // SharedPreferences 편집기

            if (viewModel.boolean!!) {
                // 즐겨찾기에서 삭제
                deleteFavorites(intent.getStringExtra("vendingMachineId")!!.toInt())
                findViewById<ImageView>(R.id.heart_image_view).setImageResource(R.drawable.ic_heart)
                viewModel.boolean = false
            } else {
                // 즐겨찾기 추가
                addFavorites(intent.getStringExtra("vendingMachineId")!!.toInt())
                findViewById<ImageView>(R.id.heart_image_view).setImageResource(R.drawable.ic_heart_fill)
                viewModel.boolean = true
            }

            // 변경된 boolean 상태를 SharedPreferences에 저장
            editor.putBoolean(
                "isFavorite_${intent.getStringExtra("vendingMachineId")}",
                viewModel.boolean!!
            )
            editor.apply()
        }
    }

    private fun setupOrderButton(vendingMachineId: String) {
        findViewById<Button>(R.id.order_button).setOnClickListener {
            // 주문 리스트를 JSON 형식으로 변환
            val orderedItemsJson = Gson().toJson(orderedItems)

            // OrderActivity로 이동
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("orderedItems", orderedItemsJson)
            intent.putExtra("vendingMachineId", vendingMachineId)
            startActivity(intent)
            finish()
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

    fun getFavorites() {
        repository.getFavorites()
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if (response.isSuccessful) {
                        Log.d("Response Success", response.code().toString())
                        Log.d("favoriteList", response.body()?.body.toString())
                    } else {
                        Log.e("Response Error", "Code: ${response.code()}")
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            Log.e("Error Body", it) // 오류 본문 로깅 추가
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                            Log.e(
                                "Error Response",
                                "Code: ${errorResponse.code}, Message: ${errorResponse.message}"
                            )
                        } ?: Log.e("Error Response", "Error body is null")
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("Network Failure", t.message ?: "Unknown error")
                }
            })
    }

    private fun clearSharedPreferences() {
        val sharedPreferences = getSharedPreferences("FavoritePreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // 모든 데이터를 삭제
        editor.apply() // 변경사항 저장
        Log.d("SharedPreferences", "All data cleared.")
    }

    fun addFavorites(machineId: Int) {
        repository.addFavorites(machineId)
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("abc", responseBody.toString())

                    } else {
                        val errorMessage = response.errorBody()?.string()
                        errorMessage?.let {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("OrderError", "Error creating order: ${t.message}")
                }
            })
    }


    fun deleteFavorites(machineId: Int) {
        repository.deleteFavorites(machineId)
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if (response.isSuccessful) {
                        Log.d("Response Success", response.code().toString())
                    } else {
                        Log.e("Response Error", "Code: ${response.code()}")
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            Log.e("Error Body", it) // 오류 본문 로깅 추가
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                            Log.e(
                                "Error Response",
                                "Code: ${errorResponse.code}, Message: ${errorResponse.message}"
                            )
                        } ?: Log.e("Error Response", "Error body is null")
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("Network Failure", t.message ?: "Unknown error")
                }
            })
    }

}
