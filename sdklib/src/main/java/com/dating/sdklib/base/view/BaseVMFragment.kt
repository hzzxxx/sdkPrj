package com.rulin.sdklib.base.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.dating.sdklib.base.viewmodel.BaseViewModel
import com.dating.sdklib.ext.toast
import com.dating.sdklib.util.log
import com.dating.sdklib.widget.dialog.LoadingDialog

abstract class BaseVMFragment<VM: BaseViewModel>: Fragment() {

    protected lateinit var mViewModel: VM

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

        initVM()
        initObserve()
        initView()
        initData(arguments)
    }

    private fun initVM() {
        mViewModel = provideVM()
        lifecycle.addObserver(mViewModel)
    }

    abstract fun provideVM(): VM

    open fun initObserve() {
        mViewModel.let { it ->
            it.toast.observe(viewLifecycleOwner, Observer {
                it?.run {
                    this.getContentIfNotHandled()?.run {
                        activity?.toast(this)
                    }
                }
            })
            it.loading.observe(viewLifecycleOwner, Observer {
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
        lifecycle.removeObserver(mViewModel)
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