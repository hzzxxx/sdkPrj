package com.dating.seekingarrangementshookupapp

import com.dating.baselib.net.RequestBuilder
import com.dating.baselib.net.RetrofitHelper
import com.dating.sdklib.base.viewmodel.BaseViewModel

class MainActivityViewModel(): BaseViewModel() {

    fun register() {
        call(
            {
                RetrofitHelper.getApiService().register(RequestBuilder.register(platform = "apple", uid = ""))
            },
            success = {
                println("onSuccess")
            }, error = {
                println("onError")
            }
        )
    }
}