package com.dating.baselib.net

import com.dating.sdklib.util.TimeUtils
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object RequestBuilder {

    private val gson by lazy { Gson() }

    private fun <K, V> buildJsonRequestBody(map: Map<K, V>): RequestBody {
        // 设置请求类型
        val mediaType = "application/json;charset=UTF-8".toMediaTypeOrNull()
        return gson.toJson(map).toRequestBody(mediaType)
    }

    fun register(
        name: String? = null,
        platform: String,        // apple / facebook
        uid: String,
        sex: Int? = null,
        images: Int? = null,
        birthday: String? = null,
        identityToken: String? = null,
        seeking: Int? = null     // 0全 1男 2女（如果不传，默认为0）
    ): RequestBody {

        val map = HashMap<String, Any>()
        name?.let { map["name"] = it }
        map["platform"] = platform
        map["uid"] = uid
        sex?.let { map["sex"] = sex }
        images?.let { map["images"] = images }
        birthday?.let { map["birthday"] = birthday }
        identityToken?.let { map["identityToken"] = identityToken }
        seeking?.let { map["seeking"] = seeking }
        return buildJsonRequestBody(getFullParam(NetConfig.REGISTER, map))
    }

    /**
     * 完整的请求参数
     */
    private fun getFullParam(action: String, content: HashMap<String, Any>): Map<String, Any> {
        val requestMap = HashMap<String, Any>()

        val paramMap = HashMap<String, Any>()

        val paramCommon = HashMap<String, Any>()
        paramCommon["action"] = action
        paramCommon["reqtime"] = TimeUtils.getCurrTimestamp()

        paramMap["common"] = paramCommon
        paramMap["content"] = content

        requestMap["request"] = paramMap

        return requestMap
    }
}