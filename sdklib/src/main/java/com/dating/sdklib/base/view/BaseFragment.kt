package com.rulin.sdklib.base.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dating.sdklib.util.log
import com.dating.sdklib.widget.dialog.LoadingDialog

abstract class BaseFragment: Fragment() {

    protected lateinit var mActivity: Activity

    protected var mLoadingDialog: LoadingDialog? = null

    protected val className: String by lazy { javaClass.simpleName }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mActivity = activity!!
        initView()
        initData(arguments)
    }

    protected fun startLoading() {
        println("startLoading.....")
        activity?.run {
            if (mLoadingDialog == null) {
                mLoadingDialog = LoadingDialog(this)
            }
            mLoadingDialog?.run {
                if (!isShowing) {
                    show()
                }
            }
        }
    }
    protected fun endLoading() {
        println("endLoading.....")
        mLoadingDialog?.apply {
            if (isShowing)
                dismissAndDestroy()
        }
        mLoadingDialog = null
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun initData(bundle: Bundle?)

    override fun onDestroy() {
        super.onDestroy()
        mLoadingDialog?.run {
            if (isShowing)
                dismissAndDestroy()
        }
        mLoadingDialog = null
    }

    private var executed = false;

    override fun onResume() {
        super.onResume()
        log(className, "onResume")
        if (!executed && view != null) {
            executed = true
            onFirstVisible()
        }
    }

    open fun onFirstVisible(){}
}