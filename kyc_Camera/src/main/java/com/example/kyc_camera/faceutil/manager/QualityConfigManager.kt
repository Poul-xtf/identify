package com.example.kyc_camera.faceutil.manager

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.baidu.idl.face.platform.utils.FileUtils
import com.example.kyc_camera.faceutil.model.QualityConfig
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * 质量检测配置管理类
 */
class QualityConfigManager private constructor() {
    var config: QualityConfig?
        private set

    fun readQualityFile(context: Context, qualityGrade: Int) {
        try {
            var json = FileUtils.readAssetFileUtf8String(context.assets, FILE_NAME_QUALITY)
            val jsonObject = JSONObject(json)
            var newObject: JSONObject? = null
            when (qualityGrade) {
                0 -> {  // normal
                    newObject = jsonObject.optJSONObject("normal")
                }
                1 -> {  // low
                    newObject = jsonObject.optJSONObject("loose")
                }
                2 -> {  // high
                    newObject = jsonObject.optJSONObject("strict")
                }
                3 -> {  // custom
                    json = FileUtils.readFileText(context.filesDir.toString() + "/" + FILE_NAME_CUSTOM)
                    newObject = if (TextUtils.isEmpty(json)) {
                        jsonObject.optJSONObject("normal")
                    } else {
                        JSONObject(json)
                    }
                }
            }
            config!!.parseFromJSONObject(newObject!!)
        } catch (e: IOException) {
            Log.e(this.javaClass.name, "初始配置读取失败", e)
            config = null
        } catch (e: JSONException) {
            Log.e(this.javaClass.name, "初始配置读取失败, JSON格式不正确", e)
            config = null
        } catch (e: Exception) {
            Log.e(this.javaClass.name, "初始配置读取失败, JSON格式不正确", e)
            config = null
        }
    }

    companion object {
        private const val FILE_NAME_QUALITY = "quality_config.json"
        private const val FILE_NAME_CUSTOM = "custom_quality.txt"
        var instance: QualityConfigManager? = null
            get() {
                if (field == null) {
                    synchronized(QualityConfigManager::class.java) {
                        if (field == null) {
                            field = QualityConfigManager()
                        }
                    }
                }
                return field
            }
            private set
    }

    init {
        config = QualityConfig()
    }
}