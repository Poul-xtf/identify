package com.wotransfer.identify.view

import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.wotransfer.identify.Constants.Companion.KYC_TAG
import com.wotransfer.identify.R
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.util.*
import com.wotransfer.identify.view.util.EnumStatus
import com.wotransfer.identify.view.util.EnumType
import kotlinx.android.synthetic.main.camera_impl_view.view.*
import java.io.IOException
import kotlin.math.min

class CameraPreviewImplView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs),
    ReferenceTypeListener {

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
        camera_crop.setImageBitmap(null)
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
     * 拍照成功-开始剪裁
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
            camera_p1.visibility = View.GONE
            iv_crop.visibility = View.GONE
            val bitmap = BitmapFactory.decodeFile(resultUrl)
            camera_crop.setImageBitmap(bitmap)
            camera_p1.uploadCard(resultUrl)
            observerList[EnumStatus.CAMERA_REPEAT]!!.stateChange(EnumType.CARD, true, "")
            return
        } catch (e: IOException) {
            e.printStackTrace()
        }
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
        }
        if (statusMap[EnumType.FACE]!!) {//判断是否需要人脸识别
            startFaceReference()
        }
    }

    //证件认证成功
    override fun onSuccess(temp: String) {
        //todo 将ocr识别返回的数据返回给业务
        
        if (statusMap[EnumType.FACE]!!) {//判断是否需要人脸识别
            ocrData = temp
            startFaceReference()
        } else {
            //反馈给业务
            observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.CARD, true, ocrData)
        }
    }


    //人脸识别拍照-百度sdk
    private fun startFaceReference() {
        Log.d(KYC_TAG, "人脸识别")
        if (observerList.isEmpty()) {
            throw IllegalStateException("Observer is not registered")
            return
        }
        observerList[EnumStatus.CAMERA_TYPE]!!.stateChange(EnumType.FACE, true, ocrData)
    }

}




