package com.wotransfer.identify.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import com.wotransfer.identify.Constants.Companion.FILE_PIC_NAME
import com.wotransfer.identify.view.Camera2Preview
import java.io.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


var mContext: Context? = null
var resultUrl: String? = null

fun getBitmapOption(
    context: Context,
    imgPath: String,
    mSurfaceViewWidth: Int,
    mSurfaceViewHeight: Int,
    mCameraID: Int,
    url: String?,
    vararg view: View,
) {
    mContext = context
    resultUrl = url
    val degree: Int = readPictureDegree(imgPath)
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(imgPath, options)
    val picWidth = max(options.outWidth, options.outHeight)
    val picHeight = min(options.outWidth, options.outHeight)
    //计算裁剪位置
    val left: Float = view[0].left.toFloat() / mSurfaceViewWidth.toFloat()
    val top: Float =
        (view[1].top.toFloat() - view[2].top.toFloat()) / mSurfaceViewHeight.toFloat()
    val right: Float = view[0].right.toFloat() / mSurfaceViewWidth.toFloat()
    val bottom: Float =
        (view[1].bottom.toFloat()) / mSurfaceViewHeight.toFloat()
    var scaleX = 1
    if (Camera2Preview.SIGNING_PIC_WIDTH in 1 until picWidth) {
        scaleX = picWidth / Camera2Preview.SIGNING_PIC_WIDTH
    }
    var scaleY = 1
    if (Camera2Preview.SIGNING_PIC_HEIGHT in 1 until picHeight) {
        scaleY = picHeight / Camera2Preview.SIGNING_PIC_HEIGHT
    }
    val inSampleSize = min(scaleX, scaleY)
    if (inSampleSize == 1) {
        options.inSampleSize = inSampleSize
    } else {
        options.inSampleSize =
            2.0.pow((Integer.toBinaryString(inSampleSize).length - 1).toDouble()).toInt()
        options.inPreferredConfig = Bitmap.Config.RGB_565
    }
    val x = (left * picHeight).toInt() / options.inSampleSize
    val y = (top * picWidth).toInt() / options.inSampleSize
    val width = ((right - left) * picHeight).toInt() / options.inSampleSize
    val height = ((bottom - top) * picWidth).toInt() / options.inSampleSize

    //设置做真实解码
    options.inJustDecodeBounds = false
    var cropBitmap = BitmapFactory.decodeFile(imgPath, options)
    val matrix = Matrix()
    if (mCameraID == CameraCharacteristics.LENS_FACING_FRONT) {
        matrix.postScale(-1f, 1f) //前置摄像头镜像翻转
        matrix.postRotate(degree.toFloat()) // 图片旋转90度；camera生成的图片和预览方向都是以手机屏幕右上角为原点向下为X轴正方向，向左为Y轴正方向的坐标系，所以预览方向和生成图片后都需要旋转90度
    } else {
        matrix.postRotate(degree.toFloat()) // 图片旋转90度；camera生成的图片和预览方向都是以手机屏幕右上角为原点向下为X轴正方向，向左为Y轴正方向的坐标系，所以预览方向和生成图片后都需要旋转90度
    }
    cropBitmap = Bitmap.createBitmap(cropBitmap,
        0,
        0,
        cropBitmap.width,
        cropBitmap.height,
        matrix,
        true)
    if (x + width <= cropBitmap.width) {
        //裁剪及保存到文件
        cropBitmap = Bitmap.createBitmap(cropBitmap,
            x,
            y,
            width,
            height)
    }
    val cropFile: File = getCropFile()
    val bos = BufferedOutputStream(FileOutputStream(cropFile))
    cropBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    bos.flush()
    bos.close()
    if (!cropBitmap.isRecycled) {
        cropBitmap.recycle()
        System.gc()
    }
}


/** 保存方法  */
fun saveBitmap(bm: Bitmap): File? {
    val f = File("/sdcard/", "pic.png")
    if (f.exists()) {
        f.delete()
    }
    try {
        val out = FileOutputStream(f)
        bm.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.flush()
        out.close()
        return f
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}


/**
 * @return 拍摄图片裁剪文件
 */
private fun getCropFile(): File {
    return if (TextUtils.isEmpty(resultUrl) || TextUtils.isEmpty(resultUrl?.trim { it <= ' ' })) {
        if (isExternalStorageExist()) {
            File(mContext?.getExternalFilesDir(""), FILE_PIC_NAME)
        } else {
            File(mContext?.filesDir, FILE_PIC_NAME)
        }
    } else {
        File(resultUrl)
    }
}


/**
 * 选择相册剪裁
 */
fun cropImage(context: Context, uri: Uri): Intent {
    val intent = Intent("com.android.camera.action.CROP")
    intent.setDataAndType(uri, "image/*")
    intent.putExtra("crop", "true")
    if (Build.MANUFACTURER.contains("HUAWEI")) {
        //硬件厂商为华为的，默认是圆形裁剪框，这里让它无法成圆形
        intent.putExtra("aspectX", 9999)
        intent.putExtra("aspectY", 9998)
    } else {
        //其他手机一般默认为方形
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
    }

    // 设置裁剪区域的形状，默认为矩形，也可设置为圆形，可能无效
    //intent.putExtra("circleCrop", true);
    intent.putExtra("scale", true)
    val cropFile = File("${context.getExternalFilesDir(null)}/crop_image.jpg")
    try {
        if (cropFile.exists()) {
            cropFile.delete()
        }
        cropFile.createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    val cropImageUri = Uri.fromFile(cropFile)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri)
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
    intent.putExtra("return-data", false)
    return intent
}

fun getCropFile(resultUrl: String): File {
    return if (TextUtils.isEmpty(resultUrl) || TextUtils.isEmpty(resultUrl?.trim { it <= ' ' })) {
        if (isExternalStorageExist()) {
            File(mContext?.getExternalFilesDir(""), FILE_PIC_NAME)
        } else {
            File(mContext?.filesDir, FILE_PIC_NAME)
        }
    } else {
        File(resultUrl)
    }
}


