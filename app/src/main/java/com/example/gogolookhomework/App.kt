package com.example.gogolookhomework

import android.app.Application
import com.example.gogolookhomework.di.KoinModules.Companion.initKoin
import timber.log.Timber

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        initKoin(this)
        Timber.plant(Timber.DebugTree())
    }
}