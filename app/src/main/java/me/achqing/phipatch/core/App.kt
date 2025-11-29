package me.achqing.phipatch.core

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        @JvmStatic
        lateinit var instance: App
            private set

        @JvmStatic
        val appContext: Context
            get() = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Config.isFirstRun = false
    }
}