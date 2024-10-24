package com.wonchihyeon.ytt_android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wonchihyeon.ytt_android.R

class AddressBottomSheetFragment : BottomSheetDialogFragment() {

    private var initialY = 0f
    private var initialHeight = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // 바텀 시트의 레이아웃을 설정
        val view = inflater.inflate(R.layout.fragment_address_bottom_sheet, container, false)

        val bottomSheetLayout = view.findViewById<LinearLayout>(R.id.persistent_bottom_sheet)

        // 터치 리스너 설정
        bottomSheetLayout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 초기 Y 좌표와 높이 설정
                    initialY = event.rawY
                    initialHeight = bottomSheetLayout.height
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = initialY - event.rawY // 드래그 방향을 반대로 설정
                    val newHeight = (initialHeight + deltaY).toInt()
                    if (newHeight > 0) {
                        bottomSheetLayout.layoutParams.height = newHeight
                        bottomSheetLayout.requestLayout()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    // 드래그 종료 시 초기 값 초기화
                    initialY = 0f
                    initialHeight = 0
                    true
                }
                else -> false
            }
        }

        return view
    }
}
