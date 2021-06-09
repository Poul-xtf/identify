package com.wotransfer.identify.example

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.wotransfer.identify.net.HttpCallBackListener
import com.wotransfer.identify.net.startHttpRequestList
import com.wotransfer.identify_ui.reference.CameraLaunch

class MyIdentifyReferenceActivity : Activity(), HttpCallBackListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_identity_view)
    }

    fun uploadId(view: View) {
        startHttpRequestList(this, "JPN")
    }

    override fun onSuccess(path: String, content: String) {

    }

    override fun onFiled() {
    }

    override fun complete() {
    }

}