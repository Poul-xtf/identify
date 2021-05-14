package com.example.kyc_camera.view

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.hardware.Camera.PictureCallback
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import com.example.kyc_camera.view.util.openCamera

open class CameraPreview : SurfaceView, SurfaceHolder.Callback {

    private var mContext: Context? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCameraId = -1
    private var camera: Camera? = null
    private var mExceptionCallback: ExceptionInterface? = null
    private val TAG = CameraPreview::class.java.name


    interface ExceptionInterface {
        fun throwableCameraIsNull()
    }

    init {
        init()
    }
    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context,attrs) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,defStyleAttr) {
        mContext = context
        init()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr,defStyleRes) {
        mContext = context
        init()
    }


    private fun init() {
        val surfaceHolder = holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setKeepScreenOn(true)
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        //因为设置了固定屏幕方向，所以在实际使用中不会触发这个方法
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        //回收释放资源
        release()
    }


    fun setExceptionCallback(callback: ExceptionInterface) {
        mExceptionCallback = callback
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mSurfaceHolder = holder
        if (mCameraId < 0) {
            initCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
        } else {
            initCamera(mCameraId)
        }
    }

    private fun initCamera(cameraId: Int) {
        camera?.let {
            release()
        }
        mSurfaceHolder?.let {
            camera = openCamera(cameraId)
            if (camera == null) {
                camera = openCamera()
            }
            camera?.let {
                try {
                    it.setPreviewDisplay(mSurfaceHolder)
                    val parameters = it.parameters
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                        it.setDisplayOrientation(90)
                        parameters.setRotation(90)
                    } else {
                        it.setDisplayOrientation(0)
                        parameters.setRotation(0)
                    }
                    val bestSize: Camera.Size = getBestSize(parameters.supportedPreviewSizes)
                    val picBestSize: Camera.Size = getBestSize(parameters.supportedPictureSizes)
                    parameters.setPreviewSize(bestSize.width, bestSize.height)
                    parameters.setPictureSize(picBestSize.width, picBestSize.height)
                    it.parameters = parameters
                    it.startPreview()
                    focus()
                } catch (e: Exception) {
                    Log.d(TAG, "Error setting camera preview: " + e.message)
                    try {
                        val parameters = it.parameters
                        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                            it.setDisplayOrientation(90)
                            parameters.setRotation(90)
                        } else {
                            it.setDisplayOrientation(0)
                            parameters.setRotation(0)
                        }
                        it.parameters = parameters
                        it.startPreview()
                        focus()
                    } catch (e1: Exception) {
                        e.printStackTrace()
                        camera = null
                    }
                }
            } ?: let {
                mExceptionCallback?.let {
                    it.throwableCameraIsNull()
                }
            }
        } ?: let {
            mCameraId = cameraId
        }
    }

    /**
     * Android相机的预览尺寸都是4:3或者16:9，这里遍历所有支持的预览尺寸，得到16:9的最大尺寸，保证成像清晰度
     *
     * @param sizes
     * @return 最佳尺寸
     */
    private fun getBestSize(sizes: List<Camera.Size>): Camera.Size {
        var bestSize: Camera.Size? = null
        for (size in sizes) {
            if (size.width.toFloat() / size.height.toFloat() == 16.0f / 9.0f) {
                if (bestSize == null) {
                    bestSize = size
                } else {
                    if (size.width > bestSize.width) {
                        bestSize = size
                    }
                }
            }
        }

        return bestSize!!
    }

    /**
     * 对焦，在CameraActivity中触摸对焦
     */
    private fun focus(callback: AutoFocusCallback?) {
        if (camera != null) {
            camera!!.autoFocus(callback)
        }
    }

    private fun focus() {
        focus(null)
    }

    /**
     * 开关闪光灯
     *
     * @return 闪光灯是否开启
     */
    fun switchFlashLight(): Boolean {
        if (camera != null) {
            val parameters = camera!!.parameters
            return if (parameters.flashMode == Camera.Parameters.FLASH_MODE_OFF) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                camera!!.parameters = parameters
                true
            } else {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                camera!!.parameters = parameters
                false
            }
        }
        return false
    }

    /**
     * 拍摄照片
     *
     * @param pictureCallback 在pictureCallback处理拍照回调
     */
    fun takePhoto(pictureCallback: PictureCallback?) {
        if (camera != null) {
            camera!!.takePicture(null, null, pictureCallback)
        }
    }

    fun startPreview() {
        if (camera != null) {
            camera!!.startPreview()
            focus()
        }
    }


    /**
     * 释放资源
     */
    private fun release() {
        camera?.run {
            stopPreview()
            release()
            null
        }
    }
}