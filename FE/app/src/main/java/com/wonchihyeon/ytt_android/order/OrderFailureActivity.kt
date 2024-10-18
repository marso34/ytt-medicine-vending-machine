package com.wonchihyeon.ytt_android.order

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wonchihyeon.ytt_android.databinding.ActivityOrderFailureBinding

class OrderFailureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderFailureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderFailureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back 버튼 클릭 이벤트 처리
        binding.btnBack.setOnClickListener {
            finish() // 액티비티 종료
        }

        // RecyclerView 설정
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this)
        binding.rvOrderItems.adapter = OrderItemsAdapter(getOrderItems())
    }

    // 예시 주문 항목 리스트
    private fun getOrderItems(): List<OrderItem> {
        return listOf(
            OrderItem("약 이름 1개", "기본: 2,000원", "2,000원"),
            OrderItem("약 이름 2개", "기본: 1,500원", "3,000원"),
            OrderItem("약 이름 3개", "기본: 4,000원", "12,000원")
        )
    }
}
