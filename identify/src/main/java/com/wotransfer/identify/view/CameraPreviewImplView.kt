package com.wotransfer.identify.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.wotransfer.identify.Constants
import com.wotransfer.identify.Constants.Companion.KYC_TAG
import com.wotransfer.identify.R
import com.wotransfer.identify.net.*
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.util.*
import com.wotransfer.identify.view.util.EnumStatus
import com.wotransfer.identify.view.util.EnumType
import kotlinx.android.synthetic.main.camera_im_view.view.*
import java.io.IOException
import kotlin.math.min

class CameraPreviewImplView : FrameLayout,
    ReferenceTypeListener,
    HttpCallBackListener {

    private var statusMap = HashMap<EnumType, Boolean>()
    private var mContext: Context? = null

    private var observerList = HashMap<EnumStatus, StateObserver>()

    private var ocrData: String? = null

    private var mSurfaceViewWidth = 0
    private var mSurfaceViewHeight = 0
    private var mCameraID = CameraCharacteristics.LENS_FACING_BACK
    private var resultUrl: String? = null

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
        inflate(mContext, R.layout.camera_im_view, this)
        initWH()
    }

    private fun initWH() {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val screenMinSize = min(screenWidth, screenHeight).toFloat()
        val maxSize = screenMinSize / 9.0f * 16.0f
        mSurfaceViewWidth = screenMinSize.toInt()
        mSurfaceViewHeight = if (maxSize > screenHeight) screenHeight else maxSize.toInt()
        val linearParams: LayoutParams = camera_p1.layoutParams as LayoutParams
        linearParams.height = (screenMinSize / 9.0f * 16.0f).toInt()
        linearParams.width = screenMinSize.toInt()
        camera_p1.layoutParams = linearParams
    }

    fun updateCropBView(url: String?) {
        getLoadOfGlide(mContext!!, url, iv_crop)
    }

    fun updateView() {
        camera_p1.visibility = View.VISIBLE
        iv_crop.visibility = View.VISIBLE
        camera_crop.setImageBitmap(null)
    }

    fun updateBitmapView(stateObserver: StateObserver? = null, bitmap: Bitmap) {
        camera_p1.visibility = View.GONE
        iv_crop.visibility = View.INVISIBLE
        camera_crop.setImageBitmap(bitmap)
        observerList[EnumStatus.CAMERA_REPEAT]?.run {
            stateChange(EnumType.CARD, true, "")
        } ?: run {
            observerList[EnumStatus.CAMERA_REPEAT] = stateObserver!!
            observerList[EnumStatus.CAMERA_REPEAT]?.stateChange(EnumType.CARD, true, "")

        }
    }

    fun updateProgressMax(max: Int) {
        progressBar.max = max
    }

    fun updateProgress(progress: Int) {
        progressBar.progress = progress
    }

    fun updateTor() {
        camera_p1.openCloseTor()
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
                camera_crop,
                camera_crop_container,
                camera_p1
            )
            resultUrl = takePhotoResult(resultUrl)
            val bitmap = BitmapFactory.decodeFile(resultUrl)
            updateBitmapView(null, bitmap)
            return
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setResultUrl(url: String) {
        this.resultUrl = url
    }

    fun setObserver(type: EnumStatus, stateObserver: StateObserver) {
        observerList[type] = stateObserver
    }

    //认证开始
    fun takePhoto(statusMap: HashMap<EnumType, Boolean>) {
        this.statusMap = statusMap
        if (statusMap[EnumType.CARD]!!) {//判断是否需要证件认证
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                camera_p1?.startTakePicture(this)
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
        Log.d(KYC_TAG, "resultUrl=$resultUrl")
        val params = getParams(
            SpUtil.getString(mContext, Constants.COUNTRY_CODE)!!,
            SpUtil.getString(mContext, Constants.NEED_FACE)!!.toInt(),
            idType!!,
            SpUtil.getString(mContext, Constants.NEED_OCR)!!.toInt(),
            SpUtil.getString(mContext, Constants.REFERENCE)!!,
            getCropFile(mContext!!, resultUrl!!))
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


    //证件上传/认证成功
    override fun onSuccess(path: String, content: String) {
        observerStatus()
        when (path) {
            upload_identity_path -> {
                observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD_UP, true, ocrData)
            }
            reference_path -> {
                observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD, true, ocrData)
            }
        }
    }

    override fun onFiled(path: String, error: String) {
        observerStatus()
        mContext?.showToast(error)
        observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD, false, ocrData)
    }

    override fun complete() {}

    private fun observerStatus() {
        if (observerList.isEmpty()) {
            mContext?.showToast("Observer is not registered")
            return
        }
    }

}




