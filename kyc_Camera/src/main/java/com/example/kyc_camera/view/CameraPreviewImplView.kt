package com.example.kyc_camera.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.example.kyc_camera.R
import com.example.kyc_camera.faceutil.observeUtil.StateObserver
import java.util.*
import kotlin.collections.HashMap


class CameraPreviewImplView : FrameLayout, ReferenceTypeListener {

    private var cameraP: Camera2Preview? = null
    private var statusMap = HashMap<String, Boolean>()

    private var observerList = arrayListOf<StateObserver>()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        val inflate = View.inflate(context, R.layout.camera_impl_view, this)
        cameraP = inflate.findViewById(R.id.camera_p1)
    }

    fun setObserver(stateObserver: StateObserver) {
        observerList.add(stateObserver)
    }

    //认证开始
    fun takePhoto(statusMap: HashMap<String, Boolean>) {
        this.statusMap = statusMap
//        if (statusMap["card"]!!) {//判断是否需要证件认证
//            cameraP?.startTakePicture(this)
//        } else if (statusMap["face"]!!) {//判断是否需要人脸识别
//            startFaceReference()
//        }
        startFaceReference()
    }

    //证件认证成功
    override fun onSuccess() {
        if (statusMap["face"]!!) {//判断是否需要人脸识别
            startFaceReference()
        } else {
            //反馈给业务
        }
    }

    //人脸识别拍照-百度sdk
    private fun startFaceReference() {
        Log.d("xtf->", "人脸识别")
        if (observerList.isEmpty()) {
            throw IllegalStateException("Observer is not registered")
            return
        }
        observerList[0].stateChange(true)
    }
}


