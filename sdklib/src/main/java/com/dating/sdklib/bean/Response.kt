package com.dating.sdklib.bean

class Response<T> {
    var response: ResponseBody<T>? = null
}

class ResponseBody<T> {
    var info: Info? = null
    var content: T? = null
}

class Info {
    var code: Int = 0
    var msg: String? = null
}