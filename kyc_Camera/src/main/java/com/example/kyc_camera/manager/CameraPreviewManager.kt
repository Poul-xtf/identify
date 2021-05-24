package com.example.kyc_camera.manager

import com.example.kyc_camera.observeInterface.StateObserver
import com.example.kyc_camera.view.CameraPreviewImplView
import com.example.kyc_camera.view.util.EnumType

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

    fun startReferenceFace(faceStatus: Boolean): CameraPreviewManager? {
        statusMap(EnumType.FACE, faceStatus)
        return instance
    }

    //证件认证
    fun startReferenceCard(cardStatus: Boolean): CameraPreviewManager? {
        statusMap(EnumType.CARD, cardStatus)
        return instance
    }

    fun setCameraPreview(cameraPreview: CameraPreviewImplView): CameraPreviewManager? {
        this.cameraPreview = cameraPreview
        return instance
    }

    fun addObserverFaceOrCardChange(stateObserver: StateObserver): CameraPreviewManager? {
        cameraPreview?.setObserver(stateObserver)
        return instance
    }

    fun startTakePhoto() {
        cameraPreview?.takePhoto(statusMap)
    }
}


