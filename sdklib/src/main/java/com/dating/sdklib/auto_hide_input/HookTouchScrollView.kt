package com.livegirl.baselib.auto_hide_input

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import com.dating.sdklib.ext.toPx

class HookTouchScrollView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    var startScroll = false
    private var curDownX = 0f
    private var curDownY = 0f
    private val scrollMax: Int = 8.toPx()

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val curX = e.x
        val curY = e.y
        if (e.action == MotionEvent.ACTION_DOWN) {
            startScroll = false
            curDownX = curX
            curDownY = curY
        }
        if (e.action == MotionEvent.ACTION_MOVE) {
            startScroll = Math.abs(curX - curDownX) > scrollMax || Math.abs(curY - curDownY) > scrollMax
            super.onTouchEvent(e)
            return false
        }
        return if (e.action == MotionEvent.ACTION_UP && !startScroll) {
            false
        } else super.onTouchEvent(e)
    }
}