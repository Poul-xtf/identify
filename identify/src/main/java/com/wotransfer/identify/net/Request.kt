package com.wotransfer.identify.net


fun startHttpRequest(
    context: HttpCallBackListener,
    path: String,
    params: Map<String, Any>? = null,
) {
    startHttpRequestImpl(HttpManager.getInstance(HttpClient(), context), path, params)
}

//fun startHttpRequest2(
//    context: HttpCallBackListener,
//    path: String,
//    params: Map<String, Any>? = null,
//) {
//    HttpManager.getInstance(HttpClient(), context)
//        ?.setParams(params)
//        ?.request(path/*, params*/)
//}

/**
 * 提供业务进行调用
 * @param country 国家
 */
fun getListOfDocuments(
    context: HttpCallBackListener,
    country: String,
    extend: String,
) {
    val params = getParams(country, extend)
    HttpManager.getInstance(HttpClient(), context)
        ?.setParams(params)
        ?.request(identity_list_path)
}