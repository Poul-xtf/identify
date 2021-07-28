package com.wotransfer.identify.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.*
import android.os.Looper.getMainLooper
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.wotransfer.identify.R
import com.wotransfer.identify.util.CameraUtil
import com.wotransfer.identify.util.showToast
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

open class Camera2Preview : SurfaceView, SurfaceHolder.Callback, Handler.Callback {

    private var mContext: Context? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCameraId = CameraCharacteristics.LENS_FACING_FRONT
    private var camera: CameraCaptureSession? = null
    private var mImageReader: ImageReader? = null
    private var mCameraDevice: CameraDevice? = null
    private var path: String? = null
    private var mainHandler: Handler? = null
    private var childHandler: Handler? = null
    private var mCameraManager: CameraManager? = null
    private var referenceTypeListener: ReferenceTypeListener? = null

    private var resultUrl: String? = null

    companion object {
        private const val MSG_OPEN_CAMERA: Int = 1
        private const val MSG_START_PREVIEW: Int = 2
        private const val MSG_START_PICTURE: Int = 3
        private const val MSG_UPLOAD_CARD: Int = 4
        private const val MSG_REFERENCE_CARD: Int = 6
        private const val MSG_LOAD_IMG: Int = 5
        const val SIGNING_PIC_WIDTH = 1280
        const val SIGNING_PIC_HEIGHT = 720
//        var cameraOutPutSizes: Size? = null
    }

    private var permissionList = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context
        init()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        mContext = context
        init()
    }

    private fun init() {
        path = mContext?.getExternalFilesDir(null)?.absolutePath
        val surfaceHolder = holder
        surfaceHolder.addCallback(this)
        surfaceHolder.setKeepScreenOn(true)
//        CameraUtil.init(mContext!!)
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            release()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mSurfaceHolder = holder
        mSurfaceHolder!!.setFixedSize(SIGNING_PIC_WIDTH,
            SIGNING_PIC_HEIGHT)
        initCamera(mCameraId)
    }

    fun openCloseTor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager?.setTorchMode(mCameraManager!!.cameraIdList[0], true)
        }
    }

    private fun initCamera(cameraId: Int) {
        mainHandler = Handler(getMainLooper())
        val handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        childHandler = Handler(handlerThread.looper, this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraManager = mContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        }
        loadImageReader()
        if (!checkPermission()) {
            return
        }
        startOpenCamera(cameraId)
    }

    //检查权限
    private fun checkPermission(): Boolean {
        var content: String?
        permissionList.forEachIndexed { index, permission ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                content = when (index) {
                    0 -> {
                        context.getString(R.string.i_tip_camera)
                    }
                    else -> context.getString(R.string.i_tip_file)
                }
                context.showToast(content!!)
                return false
            }
        }
        return true
    }

    /**
     * imageReader after take Photo
     */
    private fun loadImageReader() {
        childHandler?.obtainMessage(MSG_LOAD_IMG)?.sendToTarget()
    }

    //上传证件照片
    fun uploadCard(resultUrl: String?) {
        this.resultUrl = resultUrl
        childHandler?.obtainMessage(MSG_UPLOAD_CARD)?.sendToTarget()
    }

    //图片认证
    fun referenceImg() {
        childHandler?.obtainMessage(MSG_REFERENCE_CARD)?.sendToTarget()
    }

    @SuppressLint("MissingPermission")
    private fun startOpenCamera(cameraId: Int) {
        childHandler?.obtainMessage(MSG_OPEN_CAMERA, cameraId)?.sendToTarget()
    }

    private fun startTakePreView() {
        childHandler?.obtainMessage(MSG_START_PREVIEW)?.sendToTarget()
    }

    /**
     *  Open camera preview
     */
    fun startTakePicture(referenceTypeListener: ReferenceTypeListener) {
        this.referenceTypeListener = referenceTypeListener
        childHandler?.obtainMessage(MSG_START_PICTURE)?.sendToTarget()
    }


    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_OPEN_CAMERA -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    openCamera(mCameraId.toString())
                } else {
                    startTakePreView()
                }
            }
            MSG_START_PREVIEW -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    takePreview()
                }
            }
            MSG_START_PICTURE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                takePicture()
            }
            MSG_LOAD_IMG -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setImageReader()
            }

            MSG_UPLOAD_CARD -> {//上传图片
//                uploadRequest()
            }
            MSG_REFERENCE_CARD -> {//图片认证
//                referenceRequest()
            }
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setImageReader() {
        mImageReader = ImageReader.newInstance(SIGNING_PIC_WIDTH,
            SIGNING_PIC_HEIGHT,
            ImageFormat.JPEG,
            1)
        mImageReader?.setOnImageAvailableListener({ reader ->
            mCameraDevice!!.close()
            val image: Image = reader.acquireNextImage()
            val buffer: ByteBuffer = image.planes[0].buffer

            Log.d("Camera2Debug", "Y-channel bytes received: " + buffer.remaining())

            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            try {
                resultUrl = "$path/pic.png"
                resultUrl?.also {
                    val fileOutputStream = FileOutputStream(File(it))
                    fileOutputStream.write(bytes, 0, bytes.size)
                    fileOutputStream.close()
                    println("success,Please find image in$path ")
                    referenceTypeListener?.onTakeSuccess(it)
                }
            } catch (ex: Exception) {
                println("Exception:$ex")
                ex.printStackTrace()

            }
        }, mainHandler)
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun openCamera(cameraId: String) {
        mCameraManager?.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                mCameraDevice = camera
                startTakePreView()
            }

            override fun onDisconnected(camera: CameraDevice) {
            }

            override fun onError(camera: CameraDevice, error: Int) {
            }

        }, mainHandler)

    }

    /**
     * start to Preview that using the camera2's createCaptureSession
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun takePreview() {
        try {
            val previewRequestBuilder: CaptureRequest.Builder =
                mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW) as CaptureRequest.Builder
            previewRequestBuilder.addTarget(mSurfaceHolder!!.surface)
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice?.createCaptureSession(
                listOf(
                    mSurfaceHolder!!.surface,
                    mImageReader!!.surface
                ), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        if (null == mCameraDevice) return
                        camera = cameraCaptureSession
                        try {
                            previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                            )
                            // 显示预览
                            val previewRequest = previewRequestBuilder.build()
                            camera?.setRepeatingRequest(previewRequest, null, mainHandler)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        context.showToast(mContext?.getString(R.string.i_preview_error)!!)
                    }
                }, mainHandler
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    private var mCamera: Camera? = null
//
//    private fun takeCameraPreview(cameraId: String) {
//
//        mCamera = CameraUtil.getInstance().openCamera(cameraId)
//        mCamera ?: CameraUtil.getInstance().openCamera()
//        mCamera?.apply {
//            try {
//                setPreviewDisplay(mSurfaceHolder)
//                val parameter = parameters
//                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    setDisplayOrientation(90)
//                    parameters.setRotation(90)
//                } else {
//                    setDisplayOrientation(0)
//                    parameters.setRotation(0)
//                }
//                val bestSize: Camera.Size =
//                    (CameraUtil.getInstance().getBestSize(parameters.supportedPreviewSizes)
//                        ?: 0) as Camera.Size
//                val picBestSize: Camera.Size =
//                    (CameraUtil.getInstance().getBestSize(parameters.supportedPictureSizes)
//                        ?: 0) as Camera.Size
//                parameters.setPreviewSize(bestSize.width, bestSize.height)
//                parameters.setPictureSize(picBestSize.width, picBestSize.height)
//                parameters = parameter
//                startPreview()
//            } catch (e: Exception) {
//                try {
//                    val parameter = parameters
//                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                        //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
//                        setDisplayOrientation(90)
//                        parameters.setRotation(90)
//                    } else {
//                        setDisplayOrientation(0)
//                        parameters.setRotation(0)
//                    }
//                    parameters = parameter
//                    startPreview()
//                } catch (e1: java.lang.Exception) {
//                    e.printStackTrace()
//                    null
//                }
//            }
//        }
//    }

    /**
     * start to take photo that using the camera2's capture
     * captureRequestBuilder
     * listener
     * handler getMainLooper()
     */

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun takePicture() {
        if (mCameraDevice == null) return
        val captureRequestBuilder: CaptureRequest.Builder
        try {
            captureRequestBuilder =
                mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE) as CaptureRequest.Builder
            captureRequestBuilder.addTarget(mImageReader!!.surface)
            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
            )
            // 获取手机方向
//                val rotation: Int = resources.configuration.orientation
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder[CaptureRequest.JPEG_ORIENTATION] = 90
            val mCaptureRequest = captureRequestBuilder.build()
            camera?.capture(mCaptureRequest, null, mainHandler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun release() {
        // 释放Camera资源
        mCameraDevice?.run {
            close()
            null
        }
        mSurfaceHolder = null
    }

}