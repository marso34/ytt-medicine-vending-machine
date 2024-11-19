package com.wonchihyeon.ytt_android.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.Medicine
import com.wonchihyeon.ytt_android.view.OrderActivity

class MedicineAdapter(
    private val medicineList: List<Medicine>,
    private val context: Context,
    private val vendingMachineId: String
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicineNameTextView: TextView = itemView.findViewById(R.id.tv_medicine_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.tv_price)
        private val medicineImageView: ImageView = itemView.findViewById(R.id.iv_medicine_image)

        fun bind(medicine: Medicine) {
            medicineNameTextView.text = medicine.name
            priceTextView.text = "가격: ${medicine.price} 원"
            Glide.with(itemView.context).load(medicine.imageURL).into(medicineImageView)

            // 아이템 클릭 시 OrderActivity로 이동
            itemView.setOnClickListener {
                val intent = Intent(context, OrderActivity::class.java).apply {
                    putExtra("medicineId", medicine.id)
                    putExtra("vendingMachineId", vendingMachineId)
                    putExtra("medicineName", medicine.name)
                    putExtra("price", "가격: ${medicine.price} 원")
                    putExtra("imageUrl", medicine.imageURL)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(medicineList[position])
    }

    override fun getItemCount(): Int {
        return medicineList.size
    }
}
