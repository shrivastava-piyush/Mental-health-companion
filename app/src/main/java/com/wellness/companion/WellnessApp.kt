package com.wellness.companion

import android.app.Application
import com.wellness.companion.di.AppContainer

class WellnessApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
