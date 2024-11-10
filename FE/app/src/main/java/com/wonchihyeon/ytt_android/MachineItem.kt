package com.wonchihyeon.ytt_android

import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO

data class MachineItem(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        fun fromDTO(dto: VendingMachineDTO): MachineItem {
            return MachineItem(
                id = dto.id,
                name = dto.name,
                address = dto.address,
                latitude = dto.latitude,
                longitude = dto.longitude,
            )
        }
    }
}
