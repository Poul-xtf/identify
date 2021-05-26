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
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.wotransfer.identify.R
import com.wotransfer.identify.net.HttpCallBackListener
import com.wotransfer.identify.net.HttpClient
import com.wotransfer.identify.net.HttpManager
import com.wotransfer.identify.net.getUrl
import com.wotransfer.identify.util.showToast
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer


open class Camera2Preview : SurfaceView, SurfaceHolder.Callback, Handler.Callback,
    HttpCallBackListener {

    private var mContext: Context? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCameraId = -1
    private var camera: CameraCaptureSession? = null
    private var mExceptionCallback: ExceptionInterface? = null
    private var mImageReader: ImageReader? = null
    private var mCameraDevice: CameraDevice? = null
    private var path = Environment.getExternalStorageDirectory().absolutePath
    private var mainHandler: Handler? = null
    private var childHandler: Handler? = null
    private var mCameraManager: CameraManager? = null
    private var referenceTypeListener: ReferenceTypeListener? = null

    private var resultUrl: String? = null

    companion object {
        private val TAG = Camera2Preview::class.java.name
        private const val MSG_OPEN_CAMERA: Int = 1
        private const val MSG_START_PREVIEW: Int = 2
        private const val MSG_START_PICTURE: Int = 3
        private const val MSG_UPLOAD_CARD: Int = 4
        private const val MSG_LOAD_IMG: Int = 5
        const val SIGNIN_PIC_WIDTH = 1280
        const val SIGNIN_PIC_HEIGHT = 720
    }

    private var permissionList = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    interface ExceptionInterface {
        fun throwableCameraIsNull()
    }

    fun setExceptionCallback(callback: ExceptionInterface) {
        mExceptionCallback = callback
    }

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

    @SuppressLint("MissingPermission")
    override fun handleMessage(msg: Message): Boolean {
        //to PREVIEW
        when (msg.what) {
            MSG_OPEN_CAMERA -> {
                val cameraId = if (msg.obj != 0) msg.obj as String else "0"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
            }
            MSG_START_PREVIEW -> takePreview()
            MSG_START_PICTURE -> takePicture()
            MSG_LOAD_IMG -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1)
                    mImageReader?.setOnImageAvailableListener({ reader ->
                        mCameraDevice!!.close()
                        val image: Image = reader.acquireNextImage()
                        val buffer: ByteBuffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        try {
                            resultUrl = "$path/pic.png"
                            val fileOutputStream = FileOutputStream(File(resultUrl))
                            fileOutputStream.write(bytes, 0, bytes.size)
                            fileOutputStream.close()
                            println("success,Please find image in$path ")
                            referenceTypeListener?.onTakeSuccess(resultUrl!!)
//                            takePhotoBase(path)
//                            uploadCard()
                        } catch (ex: Exception) {
                            println("Exception:$ex")
                            ex.printStackTrace()

                        }
                    }, mainHandler)
                }
            }
            MSG_UPLOAD_CARD -> {//上传图片
                HttpManager.getInstance(HttpClient(), this)?.startRequest(getUrl())
            }
        }
        return false
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        mSurfaceHolder = holder
        if (mCameraId < 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                initCamera(CameraCharacteristics.LENS_FACING_FRONT)
            }
        } else {
            initCamera(mCameraId)
        }
    }

    private fun initCamera(cameraId: Int) {
        mainHandler = Handler(getMainLooper())

        val handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        childHandler = Handler(handlerThread.looper, this)

        mCameraManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        loadImageReader()
        if (checkPermission()) {
            return
        }
        startOpenCamera(cameraId)
    }

    //检查权限
    private fun checkPermission(): Boolean {
        var content: String? = null
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
                showToast(content!!)
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

    /**
     * start to Preview that using the camera2's createCaptureSession
     */
    private fun takePreview() {
        try {
            val previewRequestBuilder: CaptureRequest.Builder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW) as CaptureRequest.Builder
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }
            previewRequestBuilder.addTarget(mSurfaceHolder!!.surface)
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                            showToast(mContext?.getString(R.string.i_preview_error)!!)
                        }
                    }, mainHandler
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * start to take photo that using the camera2's capture
     * captureRequestBuilder
     * listener
     * handler getMainLooper()
     */
    private fun takePicture() {
        if (mCameraDevice == null) return
        val captureRequestBuilder: CaptureRequest.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    }


    private fun release() {
        // 释放Camera资源
        mCameraDevice?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                close()
            }
            null
        }
    }

    override fun onSuccess(temp: String) {
        referenceTypeListener?.onSuccess(temp)
    }

    override fun onFiled() {
        TODO("Not yet implemented")
    }

    override fun complete() {
        TODO("Not yet implemented")
    }

}