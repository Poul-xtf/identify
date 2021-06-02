package com.wotransfer.identify

class Constants {
    companion object {
        const val KYC_TAG = "kyc_camera_log"
        const val url = "http://127.0.0.1/"

        const val FILE_PIC_NAME = "pictureCrop.png"
        const val APP_NAME = "PandaRemit"
        const val SOURCE = "3"

        // quality类型：0：正常、1：宽松、2：严格、3：自定义
        const val QUALITY_NORMAL = 0
        const val QUALITY_LOW = 1
        const val QUALITY_HIGH = 2
        const val QUALITY_CUSTOM = 3

        const val LICENSE_ID = "licenseId"
        const val LICENSE_FILE_NAME = "licenseFileName"
        const val FACE = "face"
        const val CARD = "card"
    }
}