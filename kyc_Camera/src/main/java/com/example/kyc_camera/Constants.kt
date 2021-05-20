package com.example.kyc_camera

class Constants {
    companion object{
        const val url="http://127.0.0.1"
        // quality类型：0：正常、1：宽松、2：严格、3：自定义
        const val QUALITY_NORMAL = 0
        const val QUALITY_LOW = 1
        const val QUALITY_HIGH = 2
        const val QUALITY_CUSTOM = 3

        // 用于sharepreference的key
        const val KEY_QUALITY_LEVEL_SAVE = "quality_save"
    }
}