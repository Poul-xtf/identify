//package com.example.kyc_camera.view
//
//import android.content.Context
//import android.os.Handler
//import android.os.Message
//import android.util.AttributeSet
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import com.example.kyc_camera.net.HttpCallBackListener
//
//open class Camera2FacePreview : SurfaceView, SurfaceHolder.Callback, Handler.Callback, HttpCallBackListener {
//    private var mContext: Context? = null
//    private var mSurfaceHolder: SurfaceHolder? = null
//
//    init {
//        init()
//    }
//
//    constructor(context: Context) : super(context) {
//        mContext = context
//        init()
//    }
//
//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        mContext = context
//        init()
//    }
//
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        mContext = context
//        init()
//    }
//
//
//    private fun init() {
//        val surfaceHolder = holder
//        surfaceHolder.addCallback(this)
//        surfaceHolder.setKeepScreenOn(true)
//    }
//
//    //    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
//    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
//    }
//
//    override fun surfaceDestroyed(holder: SurfaceHolder?) {
//    }
//
//    override fun surfaceCreated(holder: SurfaceHolder?) {
//    }
//
//    override fun handleMessage(msg: Message): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSuccess(temp: String) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onFiled() {
//        TODO("Not yet implemented")
//    }
//
//    override fun complete() {
//        TODO("Not yet implemented")
//    }
//
//}