package com.wonchihyeon.ytt_android.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.Medicine
import com.wonchihyeon.ytt_android.view.DetailMedicineActivity

class MedicineAdapter(
    private val medicineList: List<Medicine>,
    private val context: Context,
    private val vendingMachineId: String,
    private val vendingMachineName: String,
    private val vendingMachineAddress: String,
) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicineNameTextView: TextView = itemView.findViewById(R.id.medicine_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.medicine_price)
        private val medicineImageView: ImageView = itemView.findViewById(R.id.medicine_image)
        private val productCode: TextView = itemView.findViewById(R.id.productCode)

        fun bind(medicine: Medicine) {
            medicineNameTextView.text = medicine.name
            priceTextView.text = "가격: ${medicine.price} 원"

            if(!medicine.imageURL.isEmpty()) {
                Glide.with(itemView.context).load(medicine.imageURL).into(medicineImageView)
            }
            productCode.text = medicine.productCode

            // 아이템 클릭 시 OrderActivity로 이동
            itemView.setOnClickListener {
                val intent = Intent(context, DetailMedicineActivity::class.java).apply {
                    putExtra("medicineId", medicine.id)
                    putExtra("vendingMachineId", vendingMachineId)
                    putExtra("vendingMachineName", vendingMachineName)
                    putExtra("vendingMachineAddress", vendingMachineAddress)
                    putExtra("medicineName", medicine.name)
                    putExtra("price", "가격: ${medicine.price} 원")
                    putExtra("imageUrl", medicine.imageURL)
                    putExtra("productCode", medicine.productCode)
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
