package com.wotransfer.identify.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.wotransfer.identify.Constants
import com.wotransfer.identify.Constants.Companion.KYC_TAG
import com.wotransfer.identify.R
import com.wotransfer.identify.databinding.CameraImViewBinding
import com.wotransfer.identify.net.*
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.util.*
import com.wotransfer.identify.view.util.EnumStatus
import com.wotransfer.identify.view.util.EnumType
import java.io.IOException
import kotlin.math.min

class CameraPreviewImplView : FrameLayout,
    ReferenceTypeListener,
    HttpCallBackListener {

    private var statusMap = HashMap<EnumType, Boolean>()
    private var mContext: Context? = null

    private var observerList = HashMap<EnumStatus, StateObserver>()

    private var mSurfaceViewWidth = 0
    private var mSurfaceViewHeight = 0
    private var mCameraID = CameraCharacteristics.LENS_FACING_BACK
    private var resultUrl: String? = null
    lateinit var binding: CameraImViewBinding

    constructor(context: Context) : super(context) {
        mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, def: Int) : super(context, attrs, def) {
        mContext = context
        initView()
    }

    private fun initView() {
        val root = View.inflate(mContext, R.layout.camera_im_view, this)
        binding = CameraImViewBinding.bind(root)
        initWH()
    }

    private fun initWH() {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val screenMinSize = min(screenWidth, screenHeight).toFloat()
        val maxSize = screenMinSize / 9.0f * 16.0f
        mSurfaceViewWidth = screenMinSize.toInt()
        mSurfaceViewHeight = if (maxSize > screenHeight) screenHeight else maxSize.toInt()
        val linearParams: LayoutParams = binding.cameraP1.layoutParams as LayoutParams
        linearParams.height = (screenMinSize / 9.0f * 16.0f).toInt()
        linearParams.width = screenMinSize.toInt()
        binding.cameraP1.layoutParams = linearParams
    }

    fun updateCropBView(url: String?) {
        getLoadOfGlide(mContext!!, url, binding.ivCrop)
    }

    fun updateView() {
        binding.cameraP1.visibility = View.VISIBLE
        binding.ivCrop.visibility = View.VISIBLE
        binding.cameraCrop.setImageBitmap(null)
    }

    fun updateBitmapView(bitmap: Bitmap) {
        binding.cameraP1.visibility = View.GONE
        binding.ivCrop.visibility = View.INVISIBLE
        binding.cameraCrop.setImageBitmap(bitmap)
        observerList[EnumStatus.CAMERA_REPEAT]?.stateChange(EnumType.CARD, true, "")
    }

    fun updateProgressMax(max: Int) {
        binding.progressBar.max = max
    }

    fun updateProgress(progress: Int) {
        binding.progressBar.progress = progress
    }

    fun updateTor() {
        binding.cameraP1.openCloseTor()
    }

    /**
     * 拍照成功-开始剪裁
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onTakeSuccess(path: String) {
        resultUrl = path
        try {
            if (path.isEmpty() || !isFileExist(path)) {
                return
            }
            getBitmapOption(
                mContext!!,
                path,
                mSurfaceViewWidth,
                mSurfaceViewHeight,
                mCameraID,
                resultUrl,
                binding.cameraCrop,
                binding.cameraCropContainer,
                binding.cameraP1
            )
            resultUrl = takePhotoResult(resultUrl)
            val bitmap = BitmapFactory.decodeFile(resultUrl)
            updateBitmapView(bitmap)
            return
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setResultUrl(url: Uri) {
        val bitmap = BitmapFactory.decodeStream(
            mContext?.contentResolver
                ?.openInputStream(url))
        val sBitmap = saveBitmap(mContext!!, bitmap)
        this.resultUrl = sBitmap.toString()
//        this.resultUrl = takePhotoResult(url.toString())
    }

    fun setObserver(type: EnumStatus, stateObserver: StateObserver) {
        observerList[type] = stateObserver
    }

    //认证开始
    fun takePhoto(statusMap: HashMap<EnumType, Boolean>) {
        this.statusMap = statusMap
        if (statusMap[EnumType.CARD]!!) {//判断是否需要证件认证
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.cameraP1?.startTakePicture(this)
            }
            return
        } else {
            mContext?.showToast(mContext!!.getString(R.string.i_toast_card_reference))
        }
    }

    /**
     * upload img
     */
    fun uploadImg(idType: String?) {
        Log.d(KYC_TAG, "resultUrl=${resultUrl}idType=$idType")
        val params = getParams(
            SpUtil.getString(mContext, Constants.COUNTRY_CODE)!!,
            Constants.CLOSE_FACE,
            idType!!,
            SpUtil.getString(mContext, Constants.NEED_OCR)!!.toInt(),
            SpUtil.getString(mContext, Constants.REFERENCE)!!,
            getCropFile2(mContext!!, resultUrl!!))
        startHttpRequest(this, upload_identity_path, params)
    }


    /**
     * reference image
     */
    fun referenceImg() {
        val params = getReParams(
            SpUtil.getString(mContext, Constants.REFERENCE)!!
        )
        startHttpRequest(this, reference_path, params)
    }

    /**
     * cancel reference
     */
    fun cancelReference() {
        val params = getReParams(SpUtil.getString(mContext, Constants.REFERENCE)!!)
        startHttpRequest(this, cancel_reference_path, params)
    }


    //证件上传/认证成功
    override fun onSuccess(path: String, content: String) {
        Log.d("rrr",path)
        Log.d("rrr",content)
        observerStatus()
        when (path) {
            upload_identity_path -> {
                observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD_UP, true, content)
            }
            reference_path -> {
                observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD, true, content)
            }
        }
    }

    override fun onFiled(path: String, error: String) {
        observerStatus()
        mContext?.showToast(error)
        observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD, false, error)
    }

    override fun complete() {}

    private fun observerStatus() {
        if (observerList.isEmpty()) {
            mContext?.showToast("Observer is not registered")
            return
        }
    }

}




