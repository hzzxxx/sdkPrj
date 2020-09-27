package com.dating.sdklib.base.livedata

data class RequestResult<out T>(val result: Int, val data: T?) {

    companion object{
        const val RESULT_OK = 0
        const val RESULT_FAIL = 1
    }
}