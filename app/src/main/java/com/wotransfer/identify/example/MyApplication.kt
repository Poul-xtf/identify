package com.wotransfer.identify.example

import android.app.Application
import com.wotransfer.identify_ui.reference.CameraLaunch

class MyApplication : Application() {
    private var licenseId: String = "WotransferIdentify-face-android"
    private var licenseFileName: String = "WotransferIdentify-face-android"
    override fun onCreate() {
        super.onCreate()
        CameraLaunch.register(this, "PandaRemit", licenseId, licenseFileName)
    }
}