package com.wonchihyeon.ytt_android.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.wonchihyeon.ytt_android.R
import com.wonchihyeon.ytt_android.data.model.ResponseDTO
import com.wonchihyeon.ytt_android.data.model.VendingMachineDTO
import com.wonchihyeon.ytt_android.data.network.ApiService
import com.wonchihyeon.ytt_android.data.network.RetrofitAPI
import com.wonchihyeon.ytt_android.data.repository.VendingMachineRepository
import com.wonchihyeon.ytt_android.ui.VendingMachineAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VendingMachineAdapter
    private lateinit var repository: VendingMachineRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 레포지토리 초기화
        val apiService = RetrofitAPI.getRetrofit(requireContext()).create(ApiService::class.java)
        repository = VendingMachineRepository(apiService)

        // 즐겨찾기 목록 가져오기
        getFavorites()
    }

    fun getFavorites() {
        repository.getFavorites()
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(
                    call: Call<ResponseDTO>,
                    response: Response<ResponseDTO>,
                ) {
                    if (response.isSuccessful) {
                        Log.d("Favorite Response Success", response.code().toString())
                        response.body()?.let { responseBody ->
                            // Adapter에 데이터를 설정
                            val list = responseBody.body as List<LinkedTreeMap<String, Any>>

                            val gson = Gson()
                            val json = gson.toJson(list)
                            val listType = object : TypeToken<List<VendingMachineDTO>>() {}.type
                            val responseList: List<VendingMachineDTO> = gson.fromJson(json, listType)

                            // Adapter에 데이터 설정
                            adapter = VendingMachineAdapter(responseList)
                            recyclerView.adapter = adapter
                            Log.d("Favorites", responseBody.body.toString())
                        }
                    } else {
                        Log.d("Response Error", response.code().toString())
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                            Log.e("Error", "${errorResponse.code}, ${errorResponse.message}")
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ResponseDTO>,
                    t: Throwable,
                ) {
                    Log.d("Network Failure", t.message ?: "Unknown error")
                }
            })
    }

    fun deleteFavorites(machineId: Int) {
        repository.deleteFavorites(machineId)
            .enqueue(object : Callback<ResponseDTO> {
                override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {
                    if (response.isSuccessful) {
                        Log.d("Response Success", response.code().toString())
                    } else {
                        Log.e("Response Error", "Code: ${response.code()}")
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            Log.e("Error Body", it) // 오류 본문 로깅 추가
                            val gson = Gson()
                            val errorResponse = gson.fromJson(it, ResponseDTO::class.java)
                            Log.e(
                                "Error Response",
                                "Code: ${errorResponse.code}, Message: ${errorResponse.message}"
                            )
                        } ?: Log.e("Error Response", "Error body is null")
                    }
                }

                override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                    Log.e("Network Failure", t.message ?: "Unknown error")
                }
            })
    }
}
