package com.example.kyc_camera.impl

import android.media.ImageReader
import com.example.kyc_camera.view.CameraPreviewImplView

class CameraPreviewManager : BaseManager() {

    private var cameraPreview: CameraPreviewImplView? = null

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

    //人脸认证
    fun startReferenceFace(faceStatus: Boolean): CameraPreviewManager {
        super.statusMap("face",faceStatus)
        return instance!!
    }

    //证件认证
    fun startReferenceCard(cardStatus: Boolean): CameraPreviewManager {
        super.statusMap("card",cardStatus)
        return instance!!
    }

    fun setCameraPreview(cameraPreview: CameraPreviewImplView): CameraPreviewManager {
        this.cameraPreview = cameraPreview
//        this.cameraPreview?.startPhoto()
        return instance!!
    }

//    fun setPictureCallback(pictureCallback: ImageReader.OnImageAvailableListener?): CameraPreviewManager {
//        this.pictureCallback = pictureCallback
//        return instance!!
//    }

    fun startTakePhoto() {
        cameraPreview?.takePhoto(statusMap)
    }
}


