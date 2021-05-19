package com.example.kyc_camera.net

interface HttpCallBackListener {
    fun onSuccess(temp:String)
    fun onFiled()
    fun complete()
}