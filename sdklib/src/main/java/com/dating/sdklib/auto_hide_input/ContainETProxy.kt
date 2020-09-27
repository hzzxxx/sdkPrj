package com.livegirl.baselib.auto_hide_input

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.dating.sdklib.ext.toPx
import kotlin.math.abs

class ContainETProxy(val context: Context) {

    private var et: EditText? = null
    private val scrollMax = 10.toPx()

    fun dispatchTouchEvent(consume: Boolean, ev: MotionEvent?): Boolean {
        return consume or hookDispatchTouchEvent(ev, consume)
    }

    fun onTouchEvent(consume: Boolean, event: MotionEvent?): Boolean {
        return consume or hookOnTouchEvent(event)
    }

    private var startX: Float = 0F
    private var startY: Float = 0F

    private fun hookDispatchTouchEvent(ev: MotionEvent?, consume: Boolean): Boolean {
        ev?.run {
            if (action == MotionEvent.ACTION_UP && !consume) {
                Log.d("#hookDispatchTouchEvent", "hook ACTION_UP")
                hideInput()
                return true
            } else if (action == MotionEvent.ACTION_DOWN) {
                startX = ev.x
                startY = ev.y
            } else if (action == MotionEvent.ACTION_MOVE && !consume) {
                if (abs(ev.y - startY) >= scrollMax) {
                    Log.d("#hookDispatchTouchEvent", "hook ACTION_MOVE")
                    hideInput()
                    return true
                }
            }
        }
        return false
    }

    private fun hookOnTouchEvent(ev: MotionEvent?): Boolean {
        ev?.run {
            if (action == MotionEvent.ACTION_DOWN) {
                startX = ev.x
                startY = ev.y
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (abs(ev.y - startY) >= scrollMax) {
                    Log.d("#hookOnTouchEvent", "hook ACTION_MOVE")
                    hideInput()
                }
            } else if (action == MotionEvent.ACTION_UP) {
                hideInput()
            }
        }
        return true
    }

    private fun hideInput() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        et?.run {
            imm.hideSoftInputFromWindow(this.windowToken, 0) //强制隐藏键盘
        }
    }

    fun onFinishInflate(viewGroup: ViewGroup) {
        findEt(viewGroup)
    }

    private fun findEt(viewGroup: ViewGroup) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val childAt = viewGroup.getChildAt(i)
            if (childAt is EditText) {
                et = childAt
                return
            }
            if (childAt is ViewGroup) {
                findEt(childAt)
            }
        }
    }
}