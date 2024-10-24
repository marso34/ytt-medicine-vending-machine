package com.wonchihyeon.ytt_android.view.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wonchihyeon.ytt_android.R

class OrderItemsAdapter(private val items: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.basePrice.text = item.basePrice
        holder.totalPrice.text = item.totalPrice
    }

    override fun getItemCount(): Int = items.size

    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvOrderItemName)
        val basePrice: TextView = view.findViewById(R.id.tvOrderItemBasePrice)
        val totalPrice: TextView = view.findViewById(R.id.tvOrderItemTotalPrice)
    }
}
