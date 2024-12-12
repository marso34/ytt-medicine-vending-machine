// 주문내역
package com.wonchihyeon.ytt_android.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wonchihyeon.ytt_android.OrderListAdapter
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.OrderListDTO
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.viewmodel.VendingMachineViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// OrderListFragment.kt
class OrderListFragment : Fragment(R.layout.fragment_order) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var repository: VendingMachineRepository
    private lateinit var orderAdapter: OrderListAdapter

    private val viewModel by viewModels<VendingMachineViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView1)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 레포지토리 초기화
        val apiService = RetrofitAPI.getRetrofit(requireContext()).create(ApiService::class.java)
        repository = VendingMachineRepository(apiService)

        // 모든 주문 가져오기
        fetchMyOrders(null) // 원하는 상태로 호출
    }

    private fun fetchMyOrders(state: String?) {
        // state를 매개변수로 전달
        repository.getAllOrders(state).enqueue(object : Callback<ResponseDTO> {
            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("OrderSuccess", "Orders fetched: ${Gson().toJson(responseBody)}")

                    responseBody?.body?.let { orders ->
                        // orders가 List<OrderListDTO> 타입으로 변환
                        val type = object : TypeToken<List<OrderListDTO>>() {}.type
                        val orderList: List<OrderListDTO> = Gson().fromJson(Gson().toJson(orders), type)
                        Log.d("OrderListFragment", "Orders: $orderList")
                        orderAdapter = OrderListAdapter(orderList) // 클릭 리스너 추가 가능
                        recyclerView.adapter = orderAdapter
                    } ?: Log.e("OrderListFragment", "Orders body is null")
                } else {
                    val errorMessage = response.errorBody()?.string()
                    errorMessage?.let {
                        val gson = Gson()
                        val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                        Log.d("Errorbody", "${errorResponse.code} ${errorResponse.message}")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Log.e("OrderError", "Error fetching orders: ${t.message}")
            }
        })
    }
}
