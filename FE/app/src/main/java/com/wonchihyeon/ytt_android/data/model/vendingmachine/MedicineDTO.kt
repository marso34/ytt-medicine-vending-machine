package com.wonchihyeon.ytt_android.data.model.vendingmachine


data class MedicineDTO(
    val id: Long,
    val name: String,
    val productCode: String,
    val manufacturer: String,
    val efficacy: String,
    val usage: String,
    val precautions: String,
    val validityPeriod: String,
    val price: Int,
    val stock: Int,
    val imageURL: String,
    val ingredients: List<IngredientDTO>
)