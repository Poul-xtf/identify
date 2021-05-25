package com.wotransfer.identify.faceutil.model

import org.json.JSONObject

class QualityConfig {
    // 光照
    var minIllum // 最大
            = 0f
    var maxIllum // 最小
            = 0f

    // 模糊
    var blur = 0f

    // 遮挡
    var leftEyeOcclusion // 左眼
            = 0f
    var rightEyeOcclusion // 右眼
            = 0f
    var noseOcclusion // 鼻子
            = 0f
    var mouseOcclusion // 嘴巴
            = 0f
    var leftContourOcclusion // 左脸颊
            = 0f
    var rightContourOcclusion // 右脸颊
            = 0f
    var chinOcclusion // 下巴
            = 0f

    // 姿态角
    var pitch // 上下角
            = 0
    var yaw // 左右角
            = 0
    var roll // 旋转角
            = 0

    /**
     * 解析json文件的内容
     * @param jsonObject  json数据
     */
    fun parseFromJSONObject(jsonObject: JSONObject) {
        minIllum = jsonObject.optDouble("minIllum").toFloat()
        maxIllum = jsonObject.optDouble("maxIllum").toFloat()
        blur = jsonObject.optDouble("blur").toFloat()
        leftEyeOcclusion = jsonObject.optDouble("leftEyeOcclusion").toFloat()
        rightEyeOcclusion = jsonObject.optDouble("rightEyeOcclusion").toFloat()
        noseOcclusion = jsonObject.optDouble("noseOcclusion").toFloat()
        mouseOcclusion = jsonObject.optDouble("mouseOcclusion").toFloat()
        leftContourOcclusion = jsonObject.optDouble("leftContourOcclusion").toFloat()
        rightContourOcclusion = jsonObject.optDouble("rightContourOcclusion").toFloat()
        chinOcclusion = jsonObject.optDouble("chinOcclusion").toFloat()
        pitch = jsonObject.optInt("pitch")
        yaw = jsonObject.optInt("yaw")
        roll = jsonObject.optInt("roll")
    }
}