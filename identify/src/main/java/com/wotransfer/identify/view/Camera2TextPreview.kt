package com.wotransfer.identify.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.TextureView
import com.wotransfer.identify.net.HttpCallBackListener

class Camera2TextPreview(context: Context?) : TextureView(context), Handler.Callback,
    HttpCallBackListener {
    private var mContext: Context? = context

    override fun handleMessage(msg: Message): Boolean {
        return false
    }

    override fun onSuccess(temp: String) {
    }

    override fun onFiled() {
    }

    override fun complete() {
    }

}