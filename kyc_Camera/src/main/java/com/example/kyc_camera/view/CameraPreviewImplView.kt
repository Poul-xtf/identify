package com.example.kyc_camera.view

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.example.kyc_camera.R


class CameraPreviewImplView : FrameLayout {

    private var cameraP: CameraPreview? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        val inflate = View.inflate(context, R.layout.camera_impl_view, this)
        cameraP = inflate.findViewById(R.id.camera_p1)
    }

    fun startPhoto() {
        cameraP?.startPreview()
    }

    fun takePhoto(pictureCallback: Camera.PictureCallback?) {
        cameraP?.let {
            it.takePhoto(pictureCallback)
        }
    }
}

