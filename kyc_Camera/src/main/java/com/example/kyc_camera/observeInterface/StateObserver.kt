package com.example.kyc_camera.observeInterface

import com.example.kyc_camera.view.util.EnumType

interface StateObserver {
    fun stateChange(type: EnumType, state: Boolean = false, content: String?)
//    fun <T : Any?> update(msg: T,type: EnumType, state: Boolean,content:String?)
}