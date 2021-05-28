package com.wotransfer.identify.example

import android.app.Application
import com.wotransfer.identify_ui.reference.CameraLaunch

class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        com.wotransfer.identify_ui.reference.CameraLaunch.register(this)
    }
}