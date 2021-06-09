package com.wotransfer.identify.view

interface ReferenceTypeListener {
    fun onTakeSuccess(path: String)
    fun onSuccess(path: String, content: String)
}