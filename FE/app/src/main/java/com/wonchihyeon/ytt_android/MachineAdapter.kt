package com.wonchihyeon.ytt_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO

class MachineAdapter(
    private var machineList: List<VendingMachineDTO>,
    private val itemClick: (VendingMachineDTO) -> Unit
) : RecyclerView.Adapter<MachineAdapter.MachineViewHolder>() {

    inner class MachineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val machineName: TextView = itemView.findViewById(R.id.machine_name)
        private val machineAddress: TextView = itemView.findViewById(R.id.machine_address)
        private val machineState: TextView = itemView.findViewById(R.id.machine_state)

        fun bind(machine: VendingMachineDTO) {
            machineName.text = machine.name
            machineAddress.text = machine.address

            itemView.setOnClickListener { itemClick(machine) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_machine, parent, false)
        return MachineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MachineViewHolder, position: Int) {
        holder.bind(machineList[position])
    }

    override fun getItemCount(): Int {
        return machineList.size
    }

    // 데이터 업데이트 메서드
    fun updateData(newMachineList: List<VendingMachineDTO>) {
        machineList = newMachineList
        notifyDataSetChanged()
    }
}
