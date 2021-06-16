package com.wotransfer.identify.example

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.wotransfer.identify.net.HttpCallBackListener
import com.wotransfer.identify.net.bean.IdConfigForSdkRO
import com.wotransfer.identify.net.bean.IdTypeListBean
import com.wotransfer.identify.net.getListOfDocuments
import com.wotransfer.identify.reference.CameraLaunch
import com.wotransfer.identify.util.showToast

import kotlinx.android.synthetic.main.activity_main.*


@RequiresApi(Build.VERSION_CODES.KITKAT)
class MainActivity : Activity(), HttpCallBackListener {
    var model: IdConfigForSdkRO? = null
    private var reference: String? = null

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
            .startView(CameraLaunch.LaunchType.CAMERA_OCR, reference = reference, model = model)
    }

    /**
     * 人脸识别
     *
     * 必须要传licenseId，licenseFileName
     */
    fun startFace(view: View) {
        CameraLaunch()
            .startView(CameraLaunch.LaunchType.CAMERA_FACE,
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
                et_ocr.text.toString().isEmpty(),
                reference = reference,
                model = model)
    }

    /**
     * sdk界面
     */
    fun startAll(view: View) {
        CameraLaunch()
            .startView(CameraLaunch.LaunchType.CAMERA_VIEW,
                et_face.text.toString().isEmpty(),
                et_ocr.text.toString().isEmpty())
    }

    /**
     * 自定义view
     */
    fun startAllView(view: View) {
        startActivity(Intent(this, MyIdentifyReferenceActivity::class.java))
//        startActivity(Intent(this, ReferenceResultActivity::class.java))
    }

    /**
     * 获取证件列表
     */
    fun getListDocument(view: View) {
        getListOfDocuments(this, "JPN")
    }

    override fun onSuccess(path: String, content: String) {
        val gson = Gson()
        val idTypeListBean = gson.fromJson(content, IdTypeListBean::class.java)
        model = idTypeListBean.model.idConfigForSdkROList[0]
        reference = idTypeListBean.model.reference
    }

    override fun onFiled() {
        showToast(getString(R.string.i_toast_card_failed))
    }

    override fun complete() {
        showToast(getString(R.string.i_toast_request))
    }

}