package com.wotransfer.identify.example

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.wotransfer.identify.Constants
import com.wotransfer.identify.net.HttpCallBackListener
import com.wotransfer.identify.net.bean.IdConfigForSdkRO
import com.wotransfer.identify.net.bean.IdTypeListBean
import com.wotransfer.identify.net.getListOfDocuments
import com.wotransfer.identify.reference.CameraLaunch
import com.wotransfer.identify.reference.CameraLaunch.LaunchType
import com.wotransfer.identify.ui.BaseKycActivity
import com.wotransfer.identify.util.showToast
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : BaseKycActivity(), HttpCallBackListener {
    private var model: IdConfigForSdkRO? = null
    private var reference: String? = null
    private var permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    companion object {
        var PERMISSION_STORAGE_MSG = "请授予权限，否则影响部分使用功能"
        var PERMISSION_STORAGE_CODE = 110
    }

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
        if (checkPermission())
            CameraLaunch()
                .startView(LaunchType.CAMERA_OCR, reference = reference, model = model)
    }

    /**
     * 人脸识别
     *
     * 必须要传licenseId，licenseFileName
     */
    fun startFace(view: View) {
        CameraLaunch()
            .startView(LaunchType.CAMERA_FACE,
                country = "JPN")
    }

    /**
     * ocr拍照认证+人脸识别
     *
     *
     */
    fun startAllRe(view: View) {
        if (checkPermission())
            CameraLaunch()
                .startView(LaunchType.ALL,
                    Constants.OPEN_FACE,
                    Constants.OPEN_CARD,
                    reference = reference,
                    model = model)
    }

    /**
     * sdk界面
     */
    fun startAll(view: View) {
        CameraLaunch()
            .startView(LaunchType.CAMERA_VIEW,
                Constants.OPEN_FACE,
                Constants.OPEN_CARD)
    }

    /**
     * 自定义view
     */
    fun startAllView(view: View) {
        startActivity(Intent(this, MyIdentifyReferenceActivity::class.java))
    }

    /**
     * 获取证件列表
     */
    fun getListDocument(view: View) {
        getListOfDocuments(this, Constants.CHOOSE_COUNTRY)
    }

    override fun onSuccess(path: String, content: String) {
        val gson = Gson()
        val idTypeListBean = gson.fromJson(content, IdTypeListBean::class.java)
        if (idTypeListBean.model.idConfigForSdkROList.isNotEmpty()) {
            showToast(getString(R.string.i_toast_request_success))
            model = idTypeListBean.model.idConfigForSdkROList[0]
        }
        reference = idTypeListBean.model.reference
    }

    override fun onFiled() {
        showToast(getString(R.string.i_toast_card_failed))
    }

    override fun complete() {
        showToast(getString(R.string.i_toast_request))
    }

    private fun checkPermission(): Boolean {
        return if (EasyPermissions.hasPermissions(this, permissionList.toString())) {
            true
        } else {
            EasyPermissions.requestPermissions(
                this,
                PERMISSION_STORAGE_MSG,
                PERMISSION_STORAGE_CODE,
                *permissionList
            )
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}