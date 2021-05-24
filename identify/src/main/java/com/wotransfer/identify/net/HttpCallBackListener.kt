package com.wotransfer.identify.net

interface HttpCallBackListener {
    fun onSuccess(temp:String)
    fun onFiled()
    fun complete()
}