package com.wotransfer.identify.net

interface HttpCallBackListener {
    fun onSuccess(path: String, content: String)
    fun onFiled()
    fun complete()
}