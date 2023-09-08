package dev.mnascimento.kioskey.utils

import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup

class KioskeyViewGroup(context: Context?) : ViewGroup(context) {
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }
}