package com.wotransfer.identify

class Constants {
    companion object {
        const val KYC_TAG = "kyc_camera_log"
        const val url = "https://qa.pandaremit.com/"

        const val FILE_PIC_NAME = "pictureCrop.png"
        var APP_NAME = "PandaRemit"
        var license_id= ""
        var license_name = ""
        const val SOURCE = 3

        // quality类型：0：正常、1：宽松、2：严格、3：自定义
        const val QUALITY_NORMAL = 0
        const val QUALITY_LOW = 1
        const val QUALITY_HIGH = 2
        const val QUALITY_CUSTOM = 3

        const val MODEL = "model"
        const val ID_TYPE = "idType"
        const val COUNTRY = "country"
        const val COUNTRY_CODE = "countryCode"
        const val RE_STATUS = "re_status"
        const val REFERENCE = "reference"
        const val NEED_OCR = "needOcr"
        const val NEED_FACE = "needFace"
        const val LICENSE_ID = "licenseId"
        const val LICENSE_FILE_NAME = "licenseFileName"
        const val FACE = "face"
        const val CARD = "card"
    }
}