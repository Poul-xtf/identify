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
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.wotransfer.identify.Constants
import com.wotransfer.identify.Constants.Companion.KYC_TAG
import com.wotransfer.identify.R
import com.wotransfer.identify.net.*
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.util.*
import com.wotransfer.identify.view.util.EnumStatus
import com.wotransfer.identify.view.util.EnumType
import kotlinx.android.synthetic.main.camera_impl_view.view.*
import java.io.IOException
import kotlin.math.min

class CameraPreviewImplView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs),
    ReferenceTypeListener,
    HttpCallBackListener {

    private var statusMap = HashMap<EnumType, Boolean>()

    private var observerList = HashMap<EnumStatus, StateObserver>()

    private var ocrData: String? = null

    private var mSurfaceViewWidth = 0
    private var mSurfaceViewHeight = 0
    private var mCameraID = CameraCharacteristics.LENS_FACING_BACK
    private var resultUrl: String? = null

    fun updateView() {
        camera_p1.visibility = View.VISIBLE
        iv_crop.visibility = View.VISIBLE
//        rl_crop.visibility = View.VISIBLE
        camera_crop.setImageBitmap(null)
    }

    fun updateBitmapView(stateObserver: StateObserver? = null, bitmap: Bitmap) {
        camera_p1.visibility = View.GONE
        iv_crop.visibility = View.INVISIBLE
//        rl_crop.visibility = View.INVISIBLE
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

    init {
        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.camera_impl_view, this)
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

    /**
     * ????????????-????????????
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onTakeSuccess(imgPath: String) {
        resultUrl = imgPath
        try {
            if (imgPath.isEmpty() || !isFileExist(imgPath)) {
                return
            }
            getBitmapOption(
                context,
                imgPath,
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
//            camera_p1.uploadCard(resultUrl)
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

    //????????????
    fun takePhoto(statusMap: HashMap<EnumType, Boolean>) {
        this.statusMap = statusMap
        if (statusMap[EnumType.CARD]!!) {//??????????????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                camera_p1?.startTakePicture(this)
            }
            return
        }
        if (statusMap[EnumType.FACE]!!) {//??????????????????????????????
            startFaceReference()
        }
    }


    /**
     * ????????????
     */
//    fun uploadImg() {
//        camera_p1.uploadCard(resultUrl)
    //    }

    //    /**
//     * ????????????
//     */
//    fun referenceImg() {
//        camera_p1.referenceImg()
//    }

    //    override fun onSuccess(path: String, content: String) {
//        when (path) {
//            upload_identity_path -> {
//                referenceTypeListener?.onSuccess(path, content)
//            }
//            reference_path -> {
//                referenceTypeListener?.onSuccess(path, content)
//            }
//        }
//    }


    /**
     * upload img
     */
    fun uploadImg(idType: String?) {
        Log.d("xtf->", "resultUrl=$resultUrl")
        val params = getParams(Constants.APP_NAME,
            SpUtil.getString(mContext, Constants.COUNTRY_CODE)!!,
            SpUtil.getString(mContext, Constants.NEED_FACE)!!.toInt(),
            idType!!,
            SpUtil.getString(mContext, Constants.NEED_OCR)!!.toInt(),
            SpUtil.getString(mContext, Constants.REFERENCE)!!,
            getCropFile(resultUrl!!))
        startHttpRequest(this, upload_identity_path, params)
    }


    /**
     * reference image
     */
    fun referenceImg() {
        val params = getReParams(Constants.APP_NAME,
            SpUtil.getString(mContext, Constants.REFERENCE)!!
        )
        startHttpRequest(this, reference_path, params)
    }


    //????????????/????????????
    override fun onSuccess(path: String, temp: String) {
        when (path) {
            upload_identity_path -> {//????????????
                observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD_UP, true, ocrData)
            }
            reference_path -> {//????????????
                //?????????????????????????????????????????????????????????????????????
                observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD, true, ocrData)

                //todo ???ocr????????????????????????????????????
//                if (statusMap[EnumType.FACE]!!) {
//                    ocrData = temp
//                    startFaceReference()
//                } else {
//                    //???????????????
//                    observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD, true, ocrData)
//                }
            }
        }
    }

    override fun onFiled() {
        observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD, false, ocrData)
        Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show()
    }

    override fun complete() {}

    //??????????????????-??????sdk
    private fun startFaceReference() {
        Log.d(KYC_TAG, "????????????")
        if (observerList.isEmpty()) {
            throw IllegalStateException("Observer is not registered")
        }
        observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.FACE, true, ocrData)
    }

}




