package com.wotransfer.identify.example

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.wotransfer.identify.manager.CameraLaunch

import kotlinx.android.synthetic.main.activity_main.*


@RequiresApi(Build.VERSION_CODES.KITKAT)
class MainActivity : Activity() {
    private val tag = MainActivity::class.java.simpleName


    private var licenseId: String = "WotransferIdentify-face-android"
    private var licenseFileName: String = "WotransferIdentify-face-android"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * ocr拍照认证
     *
     * 参数可传可不传
     */
    fun startPhoto(view: View) {
        CameraLaunch<CameraLaunch.LaunchType>()
            .startView(CameraLaunch.LaunchType.CAMERA_OCR)
    }

    /**
     * 人脸识别
     *
     * 必须要传licenseId，licenseFileName
     */
    fun startFace(view: View) {
        CameraLaunch<CameraLaunch.LaunchType>()
            .startView(CameraLaunch.LaunchType.CAMERA_FACE,
                licenseId = licenseId,
                licenseFileName = licenseFileName)
    }

    /**
     * ocr拍照认证+人脸识别
     *
     *
     */
    fun startAll(view: View) {
        CameraLaunch<CameraLaunch.LaunchType>()
            .startView(CameraLaunch.LaunchType.ALL,
                et_face.text.toString().isEmpty(),
                et_ocr.text.toString().isEmpty(),
                licenseId,
                licenseFileName)
    }

}