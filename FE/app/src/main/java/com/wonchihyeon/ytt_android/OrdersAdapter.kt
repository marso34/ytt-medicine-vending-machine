package com.wonchihyeon.ytt_android.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.vendingmachine.MedicineDTO

class OrdersAdapter(
    private var items: MutableList<MedicineDTO>, // MutableList로 변경
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineNameTextView: TextView = itemView.findViewById(R.id.tv_medicine_name)
        val priceTextView: TextView = itemView.findViewById(R.id.tv_price)
        val stockTextView: TextView = itemView.findViewById(R.id.tv_stock)
        val cancel: ImageView = itemView.findViewById<ImageView>(R.id.cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val medicine = items[position]
        holder.medicineNameTextView.text = medicine.name
        holder.priceTextView.text = "가격: ${medicine.price} 원"
        holder.stockTextView.text = "주문수량: ${medicine.stock} 개"

        // cancel 버튼 클릭 이벤트
        holder.cancel.setOnClickListener {

            // SharedPreferences에서 삭제 처리
            removeItemFromPreferences(holder.itemView.context, medicine.id.toString())
            removeItem(position) // 해당 위치의 아이템 삭제
        }


    }

    private fun removeItemFromPreferences(context: android.content.Context, medicineId: String) {
        val sharedPreferences = context.getSharedPreferences("OrderPreferences", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("order_$medicineId") // `order_` 접두사로 저장된 데이터 삭제
        editor.apply() // 변경사항 저장
        Log.d("SharedPreferences", "Deleted item with key: order_$medicineId")
    }


    fun removeItem(position: Int) {
        items.removeAt(position) // 리스트에서 해당 항목 제거
        notifyItemRemoved(position) // RecyclerView에 변경 알림
        notifyItemRangeChanged(position, items.size) // 아이템 위치 변경 알림
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MedicineDTO>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged() // RecyclerView 갱신
    }
}
