package com.wotransfer.identify.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.wotransfer.identify.Constants.Companion.KYC_TAG
import com.wotransfer.identify.R
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.util.*
import com.wotransfer.identify.view.util.EnumType
import kotlinx.android.synthetic.main.camera_impl_view.view.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class CameraPreviewImplView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs), ReferenceTypeListener {

    private var statusMap = HashMap<EnumType, Boolean>()

    private var observerList = arrayListOf<StateObserver>()

    private var ocrData: String? = null

    private var mSurfaceViewWidth = 0
    private var mSurfaceViewHeight = 0
    private var mCameraID = CameraCharacteristics.LENS_FACING_BACK
    private var resultUrl: String? = null

    private var permissionList = arrayListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION)

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
    }

    /**
     * 拍照成功-开始剪裁
     */
    override fun onTakeSuccess(imgPath: String) {
        resultUrl = imgPath
        try {
            if (imgPath == null || imgPath.isEmpty() || !isFileExist(imgPath)) {
                return
            }
            val degree: Int = readPictureDegree(imgPath)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imgPath, options)
            val picWidth = max(options.outWidth, options.outHeight)
            val picHeight = min(options.outWidth, options.outHeight)
            //计算裁剪位置
            val left: Float = camera_crop.left.toFloat() / mSurfaceViewWidth.toFloat()
            val top: Float = (camera_crop_container.top.toFloat() - camera_p1.top.toFloat()) / mSurfaceViewHeight.toFloat()
            val right: Float = camera_crop.right.toFloat() / mSurfaceViewWidth.toFloat()
            val bottom: Float = (camera_crop_container.bottom.toFloat()) / mSurfaceViewHeight.toFloat()
            var scaleX = 1
            if (Camera2Preview.SIGNIN_PIC_WIDTH in 1 until picWidth) {
                scaleX = picWidth / Camera2Preview.SIGNIN_PIC_WIDTH
            }
            var scaleY = 1
            if (Camera2Preview.SIGNIN_PIC_HEIGHT in 1 until picHeight) {
                scaleY = picHeight / Camera2Preview.SIGNIN_PIC_HEIGHT
            }
            val inSampleSize = min(scaleX, scaleY)
            if (inSampleSize == 1) {
                options.inSampleSize = inSampleSize
            } else {
                options.inSampleSize = 2.0.pow((Integer.toBinaryString(inSampleSize).length - 1).toDouble()).toInt()
                options.inPreferredConfig = Bitmap.Config.RGB_565
            }
            val x = (left * picHeight).toInt() / options.inSampleSize
            val y = (top * picWidth).toInt() / options.inSampleSize
            val width = ((right - left) * picHeight).toInt() / options.inSampleSize
            val height = ((bottom - top) * picWidth).toInt() / options.inSampleSize

            //设置做真实解码
            options.inJustDecodeBounds = false

            //设置，解码不占用系统核心内存，随时可以释放
//            options.inInputShareable = true
//            options.inPurgeable = true
            var cropBitmap = BitmapFactory.decodeFile(imgPath, options)
            val matrix = Matrix()
            if (mCameraID == CameraCharacteristics.LENS_FACING_FRONT) {
                matrix.postScale(-1f, 1f) //前置摄像头镜像翻转
                matrix.postRotate(degree.toFloat()) // 图片旋转90度；camera生成的图片和预览方向都是以手机屏幕右上角为原点向下为X轴正方向，向左为Y轴正方向的坐标系，所以预览方向和生成图片后都需要旋转90度
            } else {
                matrix.postRotate(degree.toFloat()) // 图片旋转90度；camera生成的图片和预览方向都是以手机屏幕右上角为原点向下为X轴正方向，向左为Y轴正方向的坐标系，所以预览方向和生成图片后都需要旋转90度
            }
            cropBitmap = Bitmap.createBitmap(cropBitmap, 0, 0, cropBitmap.width, cropBitmap.height, matrix, true)
            if (x + width <= cropBitmap.width) {
                //裁剪及保存到文件
                cropBitmap = Bitmap.createBitmap(cropBitmap,
                        x,
                        y,
                        width,
                        height)
            }
            val cropFile: File = getCropFile()!!
            val bos = BufferedOutputStream(FileOutputStream(cropFile))
            cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            if (!cropBitmap.isRecycled) {
                cropBitmap.recycle()
                System.gc()
            }
            resultUrl = takePhotoResult(resultUrl)
            camera_p1.visibility = View.GONE
            iv_crop.visibility = View.GONE
            val bitmap = BitmapFactory.decodeFile(resultUrl)
            camera_crop.setImageBitmap(bitmap)
            camera_p1.uploadCard(resultUrl)
            return
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * @return 拍摄图片裁剪文件
     */
    private fun getCropFile(): File? {
        return if (TextUtils.isEmpty(resultUrl) || TextUtils.isEmpty(resultUrl?.trim { it <= ' ' })) {
            if (isExternalStorageExist()) {
                File(context.getExternalFilesDir(""), "pictureCrop.png")
            } else {
                File(context.filesDir, "pictureCrop.png")
            }
        } else {
            File(resultUrl)
        }
    }

    fun setObserver(stateObserver: StateObserver) {
        observerList.add(stateObserver)
    }

    //认证开始
    fun takePhoto(statusMap: HashMap<EnumType, Boolean>) {
        if (!checkPermission()) {
            return
        }
        this.statusMap = statusMap
        if (statusMap[EnumType.CARD]!!) {//判断是否需要证件认证
            camera_p1?.startTakePicture(this)
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
            observerList[0].stateChange(EnumType.CARD, true, temp)
        }
    }


    //人脸识别拍照-百度sdk
    private fun startFaceReference() {
        Log.d(KYC_TAG, "人脸识别")
        if (observerList.isEmpty()) {
            throw IllegalStateException("Observer is not registered")
            return
        }
        observerList[0].stateChange(EnumType.FACE, true, ocrData)
    }


    //检查权限
    private fun checkPermission(): Boolean {
        var content: String? = null
        permissionList.forEachIndexed { index, permission ->
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
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

//    //设置数据
//    fun <T : Any?> setMsg(msg: T, type: EnumType, state: Boolean, content: String?) {
//        this.notify(msg, type, state, content)
//    }
//
//    //更新数据
//    private fun <T : Any?> notify(msg: T, type: EnumType, state: Boolean, content: String?) {
//        for (iOb in this.observerList) {
//            iOb.update(msg, type, state, content)
//        }
//    }

}




