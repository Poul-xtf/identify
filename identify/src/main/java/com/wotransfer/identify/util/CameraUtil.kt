package com.wotransfer.identify.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.Camera
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi


class CameraUtil {

    companion object {
        private val ourInstance = CameraUtil()
        var mContext: Context? = null
        var cameraManager: CameraManager? = null

        fun init(context: Context) {
            if (mContext == null) {
                mContext = context
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cameraManager =
                        mContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                }

            }
        }

        fun getInstance(): CameraUtil {
            return ourInstance
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getCameraOutPutSizes(minWidth: Int, minHeight: Int): List<Size> {
        val cameraCharacteristics =
            cameraManager?.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_BACK.toString())
        val get = cameraCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        return (get?.getOutputSizes(ImageFormat.JPEG))!!.toList()
//        return (get?.getOutputSizes(SurfaceTexture::class.java))!!.toList()

    }

//    /**
//     * 5.0以下
//     */
//    /**
//     * A safe way to get an instance of the Camera object.
//     */
//    fun openCamera(): Camera? {
//        var c: Camera? = null
//        try {
//            c = Camera.open() // attempt to get a Camera instance
//        } catch (e: Exception) {
//            // Camera is not available (in use or does not exist)
//        }
//        return c // returns null if camera is unavailable
//    }
//
//    fun openCamera(cId: String): Camera? {
//        var c: Camera? = null
//        try {
//            val numberOfCameras = Camera.getNumberOfCameras() // 获取摄像头个数
//            //遍历摄像头信息
//            for (cameraId in 0 until numberOfCameras) {
//                val cameraInfo = Camera.CameraInfo()
//                Camera.getCameraInfo(cameraId, cameraInfo)
//                if (cameraInfo.facing == cId.toInt()) { //前置摄像头
//                    c = Camera.open(cameraId) //打开摄像头
//                    return c
//                }
//            }
//        } catch (e: Exception) {
//            // Camera is not available (in use or does not exist)
//        }
//        return c // returns null if camera is unavailable
//    }
//
//    /**
//     * Android相机的预览尺寸都是4:3或者16:9，这里遍历所有支持的预览尺寸，得到16:9的最大尺寸，保证成像清晰度
//     *
//     * @param sizes
//     * @return 最佳尺寸
//     */
//    fun getBestSize(sizes: List<Camera.Size>): Camera.Size? {
//        var bestSize: Camera.Size? = null
//        for (size in sizes) {
//            if (size.width.toFloat() / size.height.toFloat() == 16.0f / 9.0f) {
//                if (bestSize == null) {
//                    bestSize = size
//                } else {
//                    if (size.width > bestSize.width) {
//                        bestSize = size
//                    }
//                }
//            }
//        }
//        return bestSize
//    }
}
