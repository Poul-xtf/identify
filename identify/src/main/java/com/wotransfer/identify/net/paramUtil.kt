package com.wotransfer.identify.net

import com.wotransfer.identify.Constants
import java.io.File

/*优化点：需要重新命名*/
const val identity_list_path = "wtocr/identify/idConfig/query/sdk"//根据国家获取证件列表
const val upload_identity_path = "wtocr/identify/ocr/upload/identity"//上传证件图片
const val reference_path = "wtocr/identify/ocr/recognize/identity"//认证图片
const val cancel_reference_path = "wtocr/identify/ocr/recognize/cancel"//取消此次认证

fun getUrl(): String {
    return Constants.url
}

//根据国家获取证件列表
fun getParams(
    country: String,
    extend: String,
): Map<String, Any> {
    return mapOf(
        "appName" to Constants.APP_NAME,
        "country" to country,
        "extend" to extend,
        "source" to Constants.SOURCE
    )
}

//上传证件图片
fun getParams(
    countryCode: String,
    face: Int,
    idType: String,
    needOcr: Int,
    reference: String,
    file: File,
): Map<String, Any> {
    return mapOf(
        "appName" to Constants.APP_NAME,
        "countryCode" to countryCode,
        "face" to face,
        "idType" to idType,
        "needOcr" to needOcr,
        "reference" to reference,
        "source" to Constants.SOURCE,
        "file" to file
    )
}

fun getReParams(
    reference: String,
): Map<String, Any> {
    return mapOf(
        "appName" to Constants.APP_NAME,
        "reference" to reference,
        "source" to Constants.SOURCE
    )
}