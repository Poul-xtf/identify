package com.wotransfer.identify.view

interface ReferenceTypeListener {
    fun onSuccess(temp:String)
    fun onTakeSuccess(path:String)
}