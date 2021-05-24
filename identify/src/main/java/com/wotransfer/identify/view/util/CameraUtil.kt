package com.wotransfer.identify.view.util

import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.util.Log

/**
 * A safe way to get an instance of the Camera object.
 */
fun openCamera(): Camera? {
    var c: Camera? = null
    try {
        c = Camera.open() // attempt to get a Camera instance
    } catch (e: Exception) {
        // Camera is not available (in use or does not exist)
    }
    return c // returns null if camera is unavailable
}

fun openCamera(cId: Int): Camera? {
    var c: Camera? = null
    try {
        val numberOfCameras = Camera.getNumberOfCameras() // 获取摄像头个数
        //遍历摄像头信息
        for (cameraId in 0 until numberOfCameras) {
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(cameraId, cameraInfo)
            if (cameraInfo.facing == cId) { //前置摄像头
                c = Camera.open(cameraId) //打开摄像头
                return c
            }
        }
    } catch (e: Exception) {
        Log.d("xxx",e.toString())
        // Camera is not available (in use or does not exist)
    }
    return c // returns null if camera is unavailable
}