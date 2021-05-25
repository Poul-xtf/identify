package com.wotransfer.identify.util

import android.media.ExifInterface
import android.os.Environment
import java.io.File
import java.io.IOException

/**
 * 读取图片属性：旋转的角度
 * @param path 图片绝对路径
 * @return degree旋转的角度
 */
fun readPictureDegree(path: String?): Int {
    var degree = 0
    try {
        val exifInterface = ExifInterface(path!!)
        when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return degree
}

fun takePhotoResult(cropImagePath: String?): String? {
    var cropImagePath = cropImagePath
    if (cropImagePath != null) {
        val degree: Int = readPictureDegree(cropImagePath)
        val stringBuffer = StringBuffer()
        stringBuffer.append(cropImagePath)
        stringBuffer.replace(cropImagePath.length - 4, cropImagePath.length, ".png")

        cropImagePath = stringBuffer.toString()
    }
    return cropImagePath
}

//判断外置存储设备（如SD卡）是否存在
fun isExternalStorageExist(): Boolean {
    val status = Environment.getExternalStorageState()
    if (null == status || status.isEmpty()) {
        return false
    }
    return status == Environment.MEDIA_MOUNTED
}

/**
 * 判断文件是否存在
 */
fun isFileExist(path: String?): Boolean {
    if (null == path || path.isEmpty()) {
        return false
    }
    val file = File(path)
    return try {
        file.exists()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

