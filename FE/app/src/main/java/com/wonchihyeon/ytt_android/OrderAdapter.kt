/*
package com.wonchihyeon.ytt_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wonchihyeon.ytt_android.data.model.OrderDTO

class OrderAdapter(private val orders: Any) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val machineName: TextView = itemView.findViewById(R.id.machineName)
        private val orderState: TextView = itemView.findViewById(R.id.orderState)
        private val orderAt: TextView = itemView.findViewById(R.id.orderAt)
        private val totalPrice: TextView = itemView.findViewById(R.id.totalPrice)

        fun bind(order: OrderDTO) {
            machineName.text = order.machineName
            orderState.text = order.orderState
            orderAt.text = order.orderAt
            totalPrice.text = order.totalPrice.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_orders, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}
*/
