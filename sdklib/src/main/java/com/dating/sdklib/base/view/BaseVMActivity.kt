package com.rulin.sdklib.base.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dating.sdklib.base.viewmodel.BaseViewModel
import com.dating.sdklib.ext.toast
import com.dating.sdklib.util.statubar.StatusBarUtil
import com.dating.sdklib.widget.dialog.LoadingDialog

abstract class BaseVMActivity<VM: BaseViewModel>: AppCompatActivity() {

    protected lateinit var mViewModel: VM

    protected var mLoadingDialog: LoadingDialog? = null

    protected val className: String by lazy { javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        initVM()
        initObserve()
        initTitleBar()
        initView()
        initData(savedInstanceState)
    }

    private fun initVM() {
        mViewModel = provideVM()
        lifecycle.addObserver(mViewModel)
    }

    abstract fun provideVM(): VM

    open fun initObserve() {
        mViewModel.let { it ->
            it.toast.observe(this, Observer {
                it?.run {
                    this.getContentIfNotHandled()?.run {
                        this@BaseVMActivity.toast(this)
                    }
                }
            })
            it.loading.observe(this, Observer {
                it?.let {
                    if (it.getContentIfNotHandled() != null && it.peekContent()) {
                        startLoading()
                    } else {
                        endLoading()
                    }
                }
            })
        }
    }

    protected fun startLoading() {
        println("startLoading.....")
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this)
        }
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

    override fun onDestroy() {
        lifecycle.removeObserver(mViewModel)

        super.onDestroy()
        mLoadingDialog?.run {
            if (isShowing)
                dismissAndDestroy()
        }
        mLoadingDialog = null
    }
}
