package com.example.kyc_camera.faceutil.observeUtil

interface StateObserver {
    fun stateChange(state: Boolean = false)
}