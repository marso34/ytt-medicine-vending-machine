package com.wonchihyeon.ytt_android.view.order

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.order.OrderListDTO

class OrderListAdapter(private val orderList: List<OrderListDTO>) : RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val machineNameTextView: TextView = itemView.findViewById(R.id.machineNameTextView)
        val orderStateTextView: TextView = itemView.findViewById(R.id.orderStateTextView)
        val orderAtTextView: TextView = itemView.findViewById(R.id.orderAtTextView)
        val totalPriceTextView: TextView = itemView.findViewById(R.id.totalPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_orderlist, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.machineNameTextView.text = order.machineName
        holder.orderStateTextView.text = order.orderState
        holder.orderAtTextView.text = order.orderAt
        holder.totalPriceTextView.text = "${order.totalPrice} 원"

        // 아이템 클릭 이벤트 추가
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, OrderActivity::class.java)


            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}

