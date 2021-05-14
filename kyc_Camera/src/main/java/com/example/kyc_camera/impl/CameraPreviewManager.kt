package com.example.kyc_camera.impl

import android.hardware.Camera
import com.example.kyc_camera.view.CameraPreview
import com.example.kyc_camera.view.CameraPreviewImplView

class CameraPreviewManager {

    private var cameraPreview: CameraPreviewImplView? = null
    private var pictureCallback: Camera.PictureCallback? = null

    companion object {
        private var instance: CameraPreviewManager? = null
        fun getInstance(): CameraPreviewManager? {
            synchronized(CameraPreviewManager::class) {
                if (instance == null) {
                    instance = CameraPreviewManager()
                }
            }
            return instance
        }
    }


    fun setCameraPreview(cameraPreview: CameraPreviewImplView): CameraPreviewManager {
        this.cameraPreview = cameraPreview
        this.cameraPreview?.startPhoto()
        return instance!!
    }

    fun setPictureCallback(pictureCallback: Camera.PictureCallback?): CameraPreviewManager {
        this.pictureCallback = pictureCallback
        return instance!!
    }

    fun startTakePhoto() {
        cameraPreview?.takePhoto(pictureCallback)
    }
}