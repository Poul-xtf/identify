package com.wotransfer.identify.example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.wotransfer.identify.Constants
import com.wotransfer.identify.example.databinding.ActivityMainBinding
import com.wotransfer.identify.net.HttpCallBackListener
import com.wotransfer.identify.net.bean.IdConfigForSdkRO
import com.wotransfer.identify.net.bean.IdTypeListBean
import com.wotransfer.identify.net.getListOfDocuments
import com.wotransfer.identify.reference.CameraLaunch
import com.wotransfer.identify.reference.CameraLaunch.LaunchType
import com.wotransfer.identify.util.showToast
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : Activity(), HttpCallBackListener {
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

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * ocr拍照认证
     *
     * 参数可传可不传
     */
    fun startPhoto(view: View) {
        if (checkPermission())
            CameraLaunch()
                .startView(this, LaunchType.CAMERA_OCR, Constants.CLOSE_FACE,
                    Constants.OPEN_CARD, reference = reference, model = model, requestCode = 0)
    }


    /**
     * 人脸识别
     *
     * 必须要传licenseId，licenseFileName
     */
    fun startFace(view: View) {
        CameraLaunch()
            .startView(this, LaunchType.CAMERA_FACE,
                country = "JPN", reference = reference, requestCode = 0)
    }

    /**
     * ocr拍照认证+人脸识别
     *
     *
     */
    fun startAllRe(view: View) {
        if (checkPermission())
            CameraLaunch()
                .startView(this, LaunchType.ALL,
                    Constants.OPEN_FACE,
                    Constants.OPEN_CARD,
                    reference = reference,
                    model = model,
                    requestCode = 0)
    }


    fun startNoAllRe(view: View) {
        if (checkPermission())
            CameraLaunch()
                .startView(this, LaunchType.ALL,
                    reference = reference,
                    model = model, requestCode = 0)
    }


    /**
     * sdk界面
     */
    fun startAll(view: View) {
        CameraLaunch()
            .startView(this, LaunchType.CAMERA_VIEW,
                Constants.OPEN_FACE,
                Constants.OPEN_CARD, requestCode = 0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == -1) {
            Log.d("xxxx", data?.getStringExtra(Constants.OCR_DATA)!!)
        }
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
        val json = JSONObject()
        json.put(Constants.NATIONALITY, "")
        json.put(Constants.TARGET_COUNTRY, "")
        getListOfDocuments(this, Constants.CHOOSE_COUNTRY, json.toString())
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

    override fun onFiled(path: String, error: String) {
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