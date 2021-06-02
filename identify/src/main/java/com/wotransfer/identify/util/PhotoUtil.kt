package com.wotransfer.identify.util

import android.content.Context
import android.content.res.AssetManager
import android.media.ExifInterface
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
 * 读取图片属性：旋转的角度
 * @param path 图片绝对路径
 * @return degree旋转的角度
 */
fun readPictureDegree(path: String?): Int {
    var degree = 0
    try {
        val exifInterface = ExifInterface(path!!)
        when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)) {
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

fun getJson(fileName: String, context: Context): String? {
    //将json数据变成字符串
    val stringBuilder = StringBuilder()
    try {
        //获取assets资源管理器
        val assetManager: AssetManager = context.assets
        //通过管理器打开文件并读取
        val bf = BufferedReader(InputStreamReader(
            assetManager.open(fileName)))
        var line: String?
        while (bf.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}

