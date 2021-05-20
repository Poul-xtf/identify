package com.example.kyc_camera.faceutil.observeInterface

import com.example.kyc_camera.view.CameraPreviewImplView
import com.example.kyc_camera.view.util.EnumType

interface StateObserver {
    fun stateChange(type: EnumType, state: Boolean = false)
}