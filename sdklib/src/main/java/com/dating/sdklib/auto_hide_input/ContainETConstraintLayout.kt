package com.livegirl.baselib.auto_hide_input

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

class ContainETConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var containETProxy: ContainETProxy = ContainETProxy(context)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return containETProxy.dispatchTouchEvent(super.dispatchTouchEvent(ev), ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return containETProxy.onTouchEvent(super.onTouchEvent(event), event)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        containETProxy.onFinishInflate(this)
    }
}