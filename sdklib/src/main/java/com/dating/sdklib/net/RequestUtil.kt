package com.rulin.sdklib.net

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object RequestUtil {

    private val gson by lazy { Gson() }

    fun <K, V> buildJsonRequestBody(map: Map<K, V>): RequestBody {
        // 设置请求类型
        val mediaType = "application/json;charset=UTF-8".toMediaTypeOrNull()
        return gson.toJson(map).toRequestBody(mediaType)
    }
}