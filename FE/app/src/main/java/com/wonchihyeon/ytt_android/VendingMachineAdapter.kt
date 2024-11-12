package com.wonchihyeon.ytt_android.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO

class VendingMachineAdapter(private val items: List<VendingMachineDTO>) : RecyclerView.Adapter<VendingMachineAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val stateTextView: TextView = itemView.findViewById(R.id.tv_state)
        private val addressTextView: TextView = itemView.findViewById(R.id.tv_address)

        fun bind(item: VendingMachineDTO) {
            nameTextView.text = item.name
            stateTextView.text = item.state
            addressTextView.text = item.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vending_machine, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }


    override fun getItemCount(): Int = items.size
}
