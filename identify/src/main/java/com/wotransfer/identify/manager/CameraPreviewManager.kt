package com.wotransfer.identify.manager

import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.view.CameraPreviewImplView
import com.wotransfer.identify.view.util.EnumStatus
import com.wotransfer.identify.view.util.EnumType

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

    //人脸和证件认证
    fun startReferenceFaceOrCard(faceStatus: Boolean? = false, cardStatus: Boolean? = false): CameraPreviewManager? {
        faceStatus?.let {
            statusMap(EnumType.FACE, faceStatus)
        }
        cardStatus?.let {
            statusMap(EnumType.CARD, cardStatus)
        }
        return instance
    }

    fun setCameraPreview(cameraPreview: CameraPreviewImplView): CameraPreviewManager? {
        this.cameraPreview = cameraPreview
        return instance
    }

    fun addObserverFaceOrCardChange(stateObserver: StateObserver): CameraPreviewManager? {
        cameraPreview?.setObserver(EnumStatus.CAMERA_TYPE,stateObserver)
        return instance
    }

    fun addObserverCameraChange(stateObserver: StateObserver): CameraPreviewManager? {
        cameraPreview?.setObserver(EnumStatus.CAMERA_REPEAT,stateObserver)
        return instance
    }

    fun startTakePhoto() {
        cameraPreview?.takePhoto(statusMap)
    }
}


