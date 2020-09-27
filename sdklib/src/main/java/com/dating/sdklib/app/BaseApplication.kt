package com.dating.sdklib.app

import android.app.Application
import androidx.multidex.MultiDexApplication

abstract class BaseApplication: MultiDexApplication() {

    companion object {
        @JvmStatic
        lateinit var mInstance: Application
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this

    }
}