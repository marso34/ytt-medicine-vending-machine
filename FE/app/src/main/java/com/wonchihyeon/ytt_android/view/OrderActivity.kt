package com.wonchihyeon.ytt_android.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.wonchihyeon.ytt_android.BuildConfig
import com.wonchihyeon.ytt_android.MyWebSocketListener
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.VendingMachineClient
import com.wonchihyeon.ytt_android.data.model.OrderDTO
import com.wonchihyeon.ytt_android.data.model.OrderDetailDTO
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.AuthRepository
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.ui.adapter.OrdersAdapter
import com.wonchihyeon.ytt_android.viewmodel.MedicineViewModel
import com.wonchihyeon.ytt_android.viewmodel.VendingMachineViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class OrderActivity : AppCompatActivity() {
    private lateinit var repository: VendingMachineRepository
    private lateinit var repository1: AuthRepository
    private lateinit var recyclerViewOrders: RecyclerView
    private lateinit var adapter: OrdersAdapter
    private val PREFS_NAME = "app_prefs"
    private val TOKEN_KEY = "access_token"

    private val viewModel: VendingMachineViewModel by viewModels()
    private val viewModel2: MedicineViewModel by viewModels()

    private lateinit var orderProductCode: String
    private var orderQuantity by Delegates.notNull<Int>()

    private val orderItems: MutableList<OrderDetailDTO> = mutableListOf()

    private lateinit var client: OkHttpClient


    private lateinit var vendingMachineClient: VendingMachineClient

    // orderId를 클래스 변수로 선언
    private var orderId: String? = null

    private val apiService: ApiService =
        RetrofitAPI.getRetrofit(this).create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val vendingMachineId = intent.getStringExtra("vendingMachineId") ?: ""

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders1)
        recyclerViewOrders.layoutManager = LinearLayoutManager(this)

        adapter = OrdersAdapter(mutableListOf())
        recyclerViewOrders.adapter = adapter

        // 레포지토리 및 API 서비스 초기화
        repository = VendingMachineRepository(apiService)
        repository1 = AuthRepository(this)


        // 저장된 주문 항목 로드
        viewModel.loadOrdersFromPreferences()

        // ViewModel에서 데이터를 가져옴
        viewModel.orderedItems.observe(this) { items ->
            adapter.updateData(items)
        }

        val userid = getUserIdFromPreferences()
        fetchMyPage()


        findViewById<Button>(R.id.order_button1).setOnClickListener {
            loadAndLogSavedOrder(viewModel2.medicineId.toLong(), vendingMachineId)
            createOrder()

            removeItemFromPreferences(this)
        }

        findViewById<Button>(R.id.order_button2).setOnClickListener {
            if (!orderId.isNullOrEmpty()) {
                Log.d("orderId", "Canceling order with ID: $orderId")
                cancelOrder(orderId!!) // 저장된 orderId로 cancelOrder 호출
            } else {
                Log.e("CancelOrder", "Order ID is null or empty")
            }
        }

        // QR 코드 이미지 클릭 리스너 추가
        findViewById<ImageView>(R.id.qrCodeImageView).setOnClickListener {
            showQRCodeDialog(findViewById<ImageView>(R.id.qrCodeImageView).drawable as BitmapDrawable)
        }
    }

    private fun removeItemFromPreferences(context: android.content.Context) {
        val sharedPreferences = context.getSharedPreferences("OrderPreferences", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply() // 변경사항 저장
    }


    private fun showQRCodeDialog(bitmapDrawable: BitmapDrawable) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_qr_code) // Custom dialog layout

        val dialogImageView: ImageView = dialog.findViewById(R.id.dialogImageView) // Dialog 내 ImageView
        dialogImageView.setImageDrawable(bitmapDrawable) // QR 코드 이미지 설정

        dialog.show()
    }

    private fun loadAndLogSavedOrder(medicineId: Long, vendingMachineId: String) {
        val sharedPreferences = getSharedPreferences("OrderPreferences", MODE_PRIVATE)
        val json = sharedPreferences.getString("order_$medicineId", null)

        if (json != null) {
            val gson = Gson()
            val medicineItem = gson.fromJson(json, MedicineDTO::class.java)
            if (medicineItem != null) {
                Log.d("SavedMedicine", "Saved Medicine: $medicineItem")

                orderProductCode = medicineItem.productCode
                orderQuantity = medicineItem.stock

                Log.d("vendingMachineId", vendingMachineId.toString())
                Log.d("orderProductCode", orderProductCode)
                Log.d("orderQuantity", orderQuantity.toString())
            } else {
                Log.d("SavedMedicine", "No valid medicine found for id: $medicineId")
            }
        } else {
            Log.d("SavedMedicine", "No saved medicine found for id: $medicineId")
        }
    }

    // 주문 생성 메서드 수정
    fun createOrder() {
        val sharedPreferences = getApplication().getSharedPreferences(
            "OrderPreferences",
            android.content.Context.MODE_PRIVATE
        )
        val allEntries = sharedPreferences.all
        val orderItemsRequest = mutableListOf<OrderDetailDTO>() // 리스트로 초기화
        val gson = Gson()

        Log.d("SavedOrders", "Currently saved orders:")
        for ((key, value) in allEntries) {
            if (key.startsWith("order_")) {
                val medicineItem =
                    gson.fromJson(value as String, MedicineDTO::class.java) // 단일 객체로 처리
                Log.d(
                    "SavedOrders",
                    "Key: $key, ProductCode: ${medicineItem.productCode}, Quantity: ${medicineItem.stock}"
                )

                // orderItemsRequest에 추가
                orderItemsRequest.add(OrderDetailDTO(medicineItem.productCode, medicineItem.stock))
            }
        }

        val userId = getUserIdFromPreferences()
        val vendingMachineId = intent.getStringExtra("vendingMachineId")?.toInt() ?: 0
        val orderRequest = OrderDTO(userId, vendingMachineId, orderItemsRequest)

        apiService.createOrder(orderRequest)
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("OrderSuccess", "Order Created: ${Gson().toJson(responseBody)}")

                        // QR 코드 생성 및 orderId 저장
                        responseBody?.body?.let { body ->
                            orderId = (body as LinkedTreeMap<*, *>)["id"] as String // orderId 클래스 변수 저장
                            Log.d("OrderIdSaved", "Saved orderId: $orderId") // 디버깅용 로그
                            generateQRCode(orderId!!)

                           /*vendingMachineClient = VendingMachineClient(viewModel.vendingMachine.value!!.id.toString(), orderId.toString())
                           vendingMachineClient.connect()*/
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        errorMessage?.let {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ResponseDTO::class.java)

                            // 토스트 메시지로 오류 메시지 표시
                            Toast.makeText(this@OrderActivity, errorResponse.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("OrderError", "Error creating order: ${t.message}")
                }
            })
    }

    fun webSockect(orderId: String) {
        client = OkHttpClient()
        val request: Request = Request.Builder()
            .url("${BuildConfig.SubUrl}/wc/topic/orders/store/$orderId")
            // url = "http://13.125.128.}}15:8080/ws/topic/orders/store/$orderId"
            .build()
        val listener = MyWebSocketListener()
        client.newWebSocket(request, listener)
        client.dispatcher.executorService.shutdown()
    }

    private fun generateQRCode(orderId: String) {
        val imageView: ImageView = findViewById(R.id.qrCodeImageView)
        val barcodeEncoder = BarcodeEncoder()

        try {
            val bitmap: Bitmap =
                barcodeEncoder.encodeBitmap(orderId, BarcodeFormat.QR_CODE, 400, 400)
            imageView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.e("QRCodeError", "Error generating QR code: ${e.message}")
        }
    }

    // 주문취소
    fun cancelOrder(orderId: String) {
        // API 호출
        repository.cancelOrder(orderId)
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("OrderSuccess", "Order Created: ${Gson().toJson(responseBody)}")
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        errorMessage?.let {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                            // 에러 로그
                            Toast.makeText(this@OrderActivity, errorResponse.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("OrderError", "Error creating order: ${t.message}")
                }
            })
    }


    fun fetchMyPage() {
        repository.myPage()
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.d("abc", "Response Body: ${Gson().toJson(response.body())}")

                        if (response.body()!!.code == 200) {
                            val userId = response.body()!!.body?.let { body ->
                                (body as LinkedTreeMap<*, *>)["userid"] as? Double
                            }?.toInt() // userid를 Double에서 Int로 변환

                            // userid를 SharedPreferences에 저장
                            saveUserIdToPreferences(userId)

                            Log.d("abc", "User ID: $userId")
                        } else {
                            Log.e("abc", response.body()!!.message)
                        }
                    } else {
                        Log.e("abc", "Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("abc", "Error fetching medicine details: ${t.message}")
                }
            })
    }

    // userid를 SharedPreferences에 저장하는 메서드
    fun saveUserIdToPreferences(userId: Int?) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("userId", userId ?: -1) // userId가 null일 경우 -1로 저장
        editor.apply()
    }

    // SharedPreferences에서 userid를 꺼내는 메서드
    fun getUserIdFromPreferences(): Int {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1) // 기본값은 -1
    }
}