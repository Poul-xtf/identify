package com.wotransfer.identify.util

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi


class CameraUtil {


    companion object {
        private val ourInstance by lazy { CameraUtil() }
        private var mContext: Context? = null
        var cameraManager: CameraManager? = null
        fun init(context: Context) {
            mContext = context
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraManager =
                    mContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            }
        }

        fun getInstance(): CameraUtil {
            return ourInstance
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getCameraOutPutSizes(maxWidth: Int, maxHeight: Int): Size? {
        val aspectRatio = maxWidth.toFloat() / maxHeight
        val cameraCharacteristics =
            cameraManager?.getCameraCharacteristics(CameraCharacteristics.LENS_FACING_BACK.toString())
        val get = cameraCharacteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val toList = (get?.getOutputSizes(ImageFormat.JPEG))!!.toList()
        toList.forEach { size ->
            if (size.width.toFloat() / size.height == aspectRatio && size.height <= maxHeight && size.width <= maxWidth) {
                return size
            }
        }
        return null
//        return (get?.getOutputSizes(SurfaceTexture::class.java))!!.toList()
    }


}
