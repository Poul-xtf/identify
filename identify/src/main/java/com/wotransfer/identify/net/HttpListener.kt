package com.wotransfer.identify.net

interface HttpListener {
    fun request(path: String?, params: Map<String, Any>? = null)
}