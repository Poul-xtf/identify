package com.wotransfer.identify.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.wotransfer.identify.Constants
import com.wotransfer.identify.manager.CameraPreviewManager
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.ui.KycCameraActivity
import com.wotransfer.identify.view.util.EnumType
import kotlinx.android.synthetic.main.activity_ocr_view.*

class OcrReferenceActivity : Activity() {
    private val tag = OcrReferenceActivity::class.java.simpleName

    private var booleanFace: Boolean = false
    private var booleanCard: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr_view)
        getIntentData()
    }

    private fun getIntentData() {
        booleanFace = intent.getBooleanExtra("face", false)
        booleanCard = intent.getBooleanExtra("card", false)
    }

    fun startPhoto(view: View) {
        CameraPreviewManager.getInstance()
                ?.startReferenceFaceOrCard(booleanFace, cardStatus = booleanCard)
                ?.setCameraPreview(camera_p)
                ?.addObserverFaceOrCardChange(object : StateObserver {
                    override fun stateChange(type: EnumType, state: Boolean, content: String?) {
                        Log.d(tag, if (type == EnumType.FACE) "开始人脸识别" else "不需要人脸识别")
                        when (type) {
                            EnumType.FACE -> {//需要人脸识别认证
                                content?.let {
                                    //已进行证件认证
                                }
                                val intent = Intent(this@OcrReferenceActivity, KycCameraActivity::class.java)
                                intent.putExtra(Constants.LICENSE_ID, "WotransferIdentify-face-android")
                                intent.putExtra(Constants.LICENSE_FILE_NAME, "WotransferIdentify-face-android")
                                startActivity(intent)
                            }
                            EnumType.CARD -> {
                                //只进行了证件认证
                            }
                        }
                    }
                })
                ?.addObserverFaceOrCardChange(object : StateObserver {
                    override fun stateChange(type: EnumType, state: Boolean, content: String?) {
                        Log.d(tag, "观察者2")
                    }
                })
                ?.startTakePhoto()
    }

}