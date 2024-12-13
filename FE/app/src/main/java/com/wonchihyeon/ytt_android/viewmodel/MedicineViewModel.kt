package com.wonchihyeon.ytt_android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wonchihyeon.ytt_android.data.model.Medicine
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.MedicineRepository

class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitAPI.getRetrofit(application).create(ApiService::class.java)
    private val repository = MedicineRepository(apiService)

    // MutableLiveData의 초기값을 설정
    private val _medicine = MutableLiveData<Medicine>().apply {
        value = Medicine(1,"키메","63","",1,1,"") // Basic Medicine 객체를 초기값으로 설정
    }
    val medicine: LiveData<Medicine> get() = _medicine
    val medicineId: Int get() = _medicine.value!!.id

    // productCode와 quantity를 LiveData를 통해 접근
    val productCode: String?
        get() = _medicine.value?.productCode // null이 아닌 경우에만 접근
    val quantity: Int?
        get() = _medicine.value?.stock // null이 아닌 경우에만 접근

    // 예시: 데이터를 로드하는 메서드
    fun loadMedicineDetails(productId: String) {
        // API 호출 등을 통해 약품 정보를 가져오고, LiveData를 업데이트합니다.
    }
}
