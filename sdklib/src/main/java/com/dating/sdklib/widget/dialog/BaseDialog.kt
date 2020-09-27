package com.dating.sdklib.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import com.dating.sdklib.R


abstract class BaseDialog @JvmOverloads constructor(context: Context, themeResId: Int = R.style.custom_dialog) : Dialog(context, themeResId), DialogInterface.OnDismissListener {

    var mOnDismissListener: DialogInterface.OnDismissListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setOnDismissListener(this)
        val view: ViewGroup = View.inflate(context, getLayoutResId(), null) as ViewGroup
        setContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        onViewCreated(view)
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        mOnDismissListener = listener
    }

    /**
     * 设置ViewID
     */
    protected abstract fun getLayoutResId(): Int
    protected abstract fun onViewCreated(view: View?)
    fun dismissAndDestroy() {
        if (!isShowing) {
            return
        }
        if (context is Activity) {
            val a = context as Activity
            if (a == null || a.isDestroyed || a.isFinishing) {
                return
            }
        }
        dismiss()
        onDestroy()
    }


    override fun onDismiss(dialog: DialogInterface) {
        if (mOnDismissListener != null) {
            mOnDismissListener!!.onDismiss(dialog)
        }
    }

    open protected fun onDestroy() {
    }

    protected fun setGravity(gravity: Int) {
        val window = window
        if (window != null) {
            val attributes = window.attributes
            attributes.gravity = gravity
            window.attributes = attributes
        }
    }

    fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun dismiss() {
        super.dismiss()
    }

    override fun show() {
        val context = context
        if (context is Activity) {
            val activity = context
            if (activity == null || activity.isDestroyed || activity.isFinishing) return
        }
        super.show()
    }

    companion object {
        lateinit var TAG: String
    }

    init {
        TAG = javaClass.simpleName
    }
}