package com.wonchihyeon.ytt_android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wonchihyeon.ytt_android.OrderDetailsActivity
import com.wonchihyeon.ytt_android.R

class AddressBottomSheetFragment(private val addressText: String) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 바텀 시트의 레이아웃을 설정
        val view = inflater.inflate(R.layout.bottom_sheet_address, container, false)

        // 주소 텍스트 설정
        val addressTextView = view.findViewById<TextView>(R.id.addressTextView)
        addressTextView.text = addressText

        // 클릭 이벤트 설정
        addressTextView.setOnClickListener {
            val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
            intent.putExtra("ADDRESS", addressText)
            startActivity(intent)
            dismiss() // 바텀 시트 닫기
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // 바텀 시트의 높이를 조정
        dialog?.let { dialog ->
            val layoutParams = dialog.window?.attributes
            layoutParams?.height = (resources.displayMetrics.heightPixels - 500) // 하단에서 500dp 만큼 높이 조정
            dialog.window?.attributes = layoutParams
        }
    }
}
