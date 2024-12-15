package com.wonchihyeon.ytt_android.data.repository

import com.wonchihyeon.ytt_android.data.model.order.OrderDTO
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import retrofit2.Call

class OrderRepository(private val apiService: ApiService) {
    // OrderDTO를 매개변수로 받아서 주문을 생성하는 메서드
    fun createOrder(orderDTO: OrderDTO): Call<ResponseDTO> {
        return apiService.createOrder(orderDTO)
    }
}
