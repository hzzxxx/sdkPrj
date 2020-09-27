package com.dating.sdklib.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    fun getCurrTimestamp(pattern: String? = "yyyyMMddHHmmss"): String {
        val format = SimpleDateFormat(pattern)
        return format.format(Date())
    }
}