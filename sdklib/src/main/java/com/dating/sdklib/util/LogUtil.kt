package com.dating.sdklib.util

import android.util.Log

var ISDEBUG = true
val Marking = "||=======||:"

val Markingend = "=="

fun log(tag: String, info: String) {
    if (ISDEBUG) {
        Log.i(Marking + tag + Markingend, info)
    }
}

fun log(tClass: Class<*>, info: String) {
    if (ISDEBUG) {
        Log.i(Marking + tClass.simpleName + Markingend, info)
    }
}

fun loge(tag: String, info: String) {
    if (ISDEBUG) {
        Log.e(Marking + tag + Markingend, info)
    }
}

/**
 *
 * 使用示例
log<SignInActivity>("??????")
 */

inline fun <reified T> log(content: String) {

    if (ISDEBUG) {
        Log.i(Marking + T::class.java.simpleName + Markingend, content)
    }

}
/**
 *
 * 使用示例
log(this@SignInActivity,"22222")
 */
inline fun <reified T> log(content: T, context: String) {
    if (ISDEBUG) {
        Log.i(Marking + content + Markingend, context)
    }


}
