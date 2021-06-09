package com.wotransfer.identify.example

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.wotransfer.identify.ui.ReferenceResultActivity
import com.wotransfer.identify_ui.IdentifyReferenceActivity
import com.wotransfer.identify_ui.reference.CameraLaunch

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
        CameraLaunch()
            .startView(CameraLaunch.LaunchType.CAMERA_OCR)
    }

    /**
     * 人脸识别
     *
     * 必须要传licenseId，licenseFileName
     */
    fun startFace(view: View) {
        CameraLaunch()
            .startView(CameraLaunch.LaunchType.CAMERA_FACE,
             /*   licenseId = licenseId,
                licenseFileName = licenseFileName,*/
                country = "JPN")
    }

    /**
     * ocr拍照认证+人脸识别
     *
     *
     */
    fun startAllRe(view: View) {
        CameraLaunch()
            .startView(CameraLaunch.LaunchType.ALL,
                et_face.text.toString().isEmpty(),
                et_ocr.text.toString().isEmpty()/*,
                licenseId,
                licenseFileName*/)
    }

    /**
     * sdk界面
     */
    fun startAll(view: View) {
        CameraLaunch()
            .startView(CameraLaunch.LaunchType.CAMERA_VIEW,
                et_face.text.toString().isEmpty(),
                et_ocr.text.toString().isEmpty()/*,
                licenseId,
                licenseFileName*/)
    }

    /**
     * 自定义view
     */
    fun startAllView(view: View) {
        startActivity(Intent(this, MyIdentifyReferenceActivity::class.java))
//        startActivity(Intent(this, ReferenceResultActivity::class.java))
    }

}