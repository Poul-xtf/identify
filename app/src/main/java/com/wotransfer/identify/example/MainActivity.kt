package com.wotransfer.identify.example

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.wotransfer.identify.Constants

import com.wotransfer.identify.ui.KycCameraActivity
import kotlinx.android.synthetic.main.activity_main.*


@RequiresApi(Build.VERSION_CODES.KITKAT)
class MainActivity : Activity() {
    private val tag = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * ocr拍照认证
     */
    fun startPhoto(view: View) {
        val intent = Intent(this, OcrReferenceActivity::class.java)
        intent.putExtra(Constants.FACE, false)
        intent.putExtra(Constants.CARD, true)
        startActivity(intent)
    }

    /**
     * 人脸识别
     */
    fun startFace(view: View) {
        val intent = Intent(this, KycCameraActivity::class.java)
        intent.putExtra(Constants.LICENSE_ID, "WotransferIdentify-face-android")
        intent.putExtra(Constants.LICENSE_FILE_NAME, "WotransferIdentify-face-android")
        startActivity(intent)

    }

    fun startAll(view: View) {
        val intent = Intent(this, OcrReferenceActivity::class.java)
        intent.putExtra(Constants.FACE, et_face.text.toString().isEmpty())
        intent.putExtra(Constants.CARD, et_ocr.text.toString().isEmpty())
        startActivity(intent)
    }

}