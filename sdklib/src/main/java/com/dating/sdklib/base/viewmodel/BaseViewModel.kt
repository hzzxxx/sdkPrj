package com.dating.sdklib.base.viewmodel

import androidx.lifecycle.*
import com.dating.sdklib.base.ApiHandler
import com.dating.sdklib.bean.Response
import com.dating.sdklib.base.livedata.Event
import com.dating.sdklib.base.livedata.RequestResult
import com.dating.sdklib.base.livedata.RequestResult.Companion.RESULT_FAIL
import com.dating.sdklib.base.livedata.RequestResult.Companion.RESULT_OK
import com.dating.sdklib.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel(), LifecycleObserver {

    var className: String = javaClass.simpleName

    val toast = MutableLiveData<Event<String>>()

    val loading = MutableLiveData<Event<Boolean>>()

    fun toast(e: String) {
        toast.value = Event(e)
    }

    protected fun startLoading() {
        loading.value = Event(true)
    }

    protected fun endLoading() {
        loading.value = Event(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(owner: LifecycleOwner) {
        log("Lifecycle", "$className------onCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        log("Lifecycle", "$className------onDestroy")
    }

    fun<T> successResult(data: T) = RequestResult(RESULT_OK, data)
    fun failResult() = RequestResult(RESULT_FAIL, null)

    private fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    fun <T> call(
        block: suspend CoroutineScope.() -> Response<T>,
        success: (T?) -> Unit,
        error: (String?) -> Unit = {}
    ) {
        launchUI {
            ApiHandler.handleException(
                {
                    withContext(Dispatchers.IO) {block()}
                },
                { res ->
                    ApiHandler.executeResponse(res) {
                        success(it)
                    }
                },
                {
                    error(it)
                }
            )
        }
    }
}