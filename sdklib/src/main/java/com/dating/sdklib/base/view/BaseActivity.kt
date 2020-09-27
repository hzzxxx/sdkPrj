package com.dating.sdklib.base.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dating.sdklib.util.statubar.StatusBarUtil
import com.dating.sdklib.widget.dialog.LoadingDialog

abstract class BaseActivity: AppCompatActivity() {

    protected var mLoadingDialog: LoadingDialog? = null

    protected val className: String by lazy { javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        initTitleBar()
        initView()
        initData(savedInstanceState)
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun initData(bundle: Bundle?)

    protected fun initTitleBar() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false)
        StatusBarUtil.setTranslucentStatus(this)
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容

        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            //
            StatusBarUtil.setWindowStatusBarColor(this, android.R.color.transparent)
        }
    }

    protected fun startLoading() {
        startLoading(true)
    }

    protected fun startLoading(cancelable: Boolean) {
        println("startLoading.....")
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this)
        }
        mLoadingDialog!!.setCancelable(cancelable)
        mLoadingDialog?.run {
            if (!isShowing) {
                show()
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

    override fun onDestroy() {
        super.onDestroy()
        mLoadingDialog?.run {
            if (isShowing)
                dismissAndDestroy()
        }
        mLoadingDialog = null
    }
}
