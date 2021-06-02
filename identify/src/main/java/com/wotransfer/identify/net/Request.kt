package com.wotransfer.identify.net


fun startHttpRequest(
    context: HttpCallBackListener,
    path: String?,
    params: Map<String, Any>? = null,
) {
    startHttpRequestImpl(HttpManager.getInstance(HttpClient(), context), path, params)
}

fun startHttpRequest2(
    context: HttpCallBackListener,
    path: String?,
    params: Map<String, Any>? = null,
) {
    HttpManager.getInstance(HttpClient(), context)
        ?.setParams(params)
        ?.request(path/*, params*/)
}
