package com.wonchihyeon.ytt_android.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wonchihyeon.ytt_android.OrderDetailsActivity
import com.wonchihyeon.ytt_android.R

class AddressBottomSheetFragment() : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 바텀 시트의 레이아웃을 설정
        val view = inflater.inflate(R.layout.bottom_sheet_address, container, false)

        return view

    }
}
