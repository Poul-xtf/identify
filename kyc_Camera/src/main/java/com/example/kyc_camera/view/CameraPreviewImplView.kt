package com.example.kyc_camera.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.example.kyc_camera.Constants.Companion.KYC_TAG
import com.example.kyc_camera.R
import com.example.kyc_camera.observeInterface.StateObserver
import com.example.kyc_camera.view.util.EnumType
import kotlin.collections.HashMap

class CameraPreviewImplView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs), ReferenceTypeListener {

    private var cameraP: Camera2Preview? = null
    private var statusMap = HashMap<EnumType, Boolean>()

    private var observerList = arrayListOf<StateObserver>()

    private var ocrData: String? = null

    init {
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
    fun takePhoto(statusMap: HashMap<EnumType, Boolean>) {
        this.statusMap = statusMap
        if (statusMap[EnumType.CARD]!!) {//判断是否需要证件认证
            cameraP?.startTakePicture(this)
            return
        }
        if (statusMap[EnumType.FACE]!!) {//判断是否需要人脸识别
            startFaceReference()
        }
    }

    //证件认证成功
    override fun onSuccess(temp: String) {
        //todo 将ocr识别返回的数据返回给业务
        if (statusMap[EnumType.FACE]!!) {//判断是否需要人脸识别
            ocrData = temp
            startFaceReference()
        } else {
            //反馈给业务
            observerList[0].stateChange(EnumType.CARD, true, temp)
        }
    }

    //人脸识别拍照-百度sdk
    private fun startFaceReference() {
        Log.d(KYC_TAG, "人脸识别")
        if (observerList.isEmpty()) {
            throw IllegalStateException("Observer is not registered")
            return
        }
        observerList[0].stateChange(EnumType.FACE, true, ocrData)
    }

//    //设置数据
//    fun <T : Any?> setMsg(msg: T, type: EnumType, state: Boolean, content: String?) {
//        this.notify(msg, type, state, content)
//    }
//
//    //更新数据
//    private fun <T : Any?> notify(msg: T, type: EnumType, state: Boolean, content: String?) {
//        for (iOb in this.observerList) {
//            iOb.update(msg, type, state, content)
//        }
//    }

}




