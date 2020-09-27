package com.dating.baselib.net

import com.dating.baselib.bean.LoginResponse
import com.dating.sdklib.bean.Response
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST(NetConfig.REGISTER)
    suspend fun register(@Body requestBody: RequestBody): Response<LoginResponse>
}