package com.wotransfer.identify

class Constants {
    companion object {
        const val KYC_TAG = "kyc_camera_log"
//        const val url = "https://qa.pandaremit.com/"
        const val url = "https://atestidentify.pandaremit.com/"
        const val FILE_PIC_NAME = "pictureCrop.png"
        const val SOURCE = 3
        const val SP_KEY = "local_mess"

        // quality类型：0：正常、1：宽松、2：严格、3：自定义
        const val QUALITY_NORMAL = 0
        const val QUALITY_LOW = 1
        const val QUALITY_HIGH = 2
        const val QUALITY_CUSTOM = 3
        const val MODEL = "model"
        const val COUNTRY_CODE = "countryCode"
        const val COUNTRY_NAME = "countryName"
        const val RE_STATUS = "re_status"
        const val REFERENCE = "reference"
        const val NEED_OCR = "needOcr"
        const val NEED_FACE = "needFace"
        const val FACE = "face"
        const val CARD = "card"
        const val DEFAULT_CODE = -1
        const val OPEN_CARD = 0
        const val CLOSE_CARD = 1
        const val OPEN_FACE = 0
        const val CLOSE_FACE = 1

        var APP_NAME = "PandaRemit"
        var CHOOSE_COUNTRY = "HKG"
        var CHOOSE_COUNTRY_NAME = "中国香港"
        var LICENSE_ID = ""
        var LICENSE_NAME = ""
    }
}