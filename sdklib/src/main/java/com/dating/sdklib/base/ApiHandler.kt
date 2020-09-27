package com.dating.sdklib.base

import com.dating.sdklib.bean.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.json.JSONException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ApiHandler {
    suspend fun<T> handleException(
        block: suspend CoroutineScope.() -> Response<T>,
        success: suspend CoroutineScope.(Response<T>) -> Unit,
        error: suspend CoroutineScope.(String?) -> Unit
    ) {
        coroutineScope {
            try {
                success(block())
            } catch (e: Throwable) {
                error(parseException(e))
            }
        }
    }

    private fun parseException(e: Throwable): String? {
        return when (e) {
            is ResponseThrowable -> e.errorMsg
            is SocketTimeoutException -> "超时"
            is HttpException -> {
                when (e.code()) {
                    404 -> "没有找到合适的资源404"
                    500 -> "服务器内部错误500"
                    else -> e.message()
                }
            }
            is JSONException -> "json解析异常"
            is UnknownHostException -> "网络异常"
            else -> e.message
        }
    }

    suspend fun <T> executeResponse(
        response: Response<T>,
        success: suspend CoroutineScope.(T?) -> Unit
    ) {
        coroutineScope {
            if (response.response?.info?.code == 100000)  success(response.response?.content)
            else throw ResponseThrowable(response.response?.info?.code, response.response?.info?.msg)
        }
    }

    class ResponseThrowable(val errorCode: Int?, val errorMsg: String?): Throwable()
}