package com.dating.sdklib.auto_hide_input

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dating.sdklib.util.DisplayUtils

class HookActionUpRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var startScroll = false
    private var curDownX = 0f
    private var curDownY = 0f
    private val scrollMax = DisplayUtils.dip2px(context, 8f).toFloat()

    init {
        layoutManager = LinearLayoutManager(context)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val curX = e.x
        val curY = e.y
        if (e.action == MotionEvent.ACTION_DOWN) {
            startScroll = false
            curDownX = curX
            curDownY = curY
        }
        if (e.action == MotionEvent.ACTION_MOVE) {
            startScroll =
                Math.abs(curX - curDownX) > scrollMax || Math.abs(curY - curDownY) > scrollMax
            super.onTouchEvent(e)
            return false
        }
        return if (e.action == MotionEvent.ACTION_UP && !startScroll) {
            false
        } else super.onTouchEvent(e)
    }
}