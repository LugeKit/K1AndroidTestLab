package com.k1.common

import android.app.Application
import androidx.annotation.CallSuper

open class BaseApplication: Application() {

    companion object {
        lateinit var instance: BaseApplication
            private set
    }

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}