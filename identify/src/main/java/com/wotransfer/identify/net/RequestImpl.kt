package com.wotransfer.identify.net

fun startHttpRequestImpl(
    httpListener: HttpListener?,
    path: String?,
    params: Map<String, Any>? = null,
) {
    httpListener?.request(path, params)
}