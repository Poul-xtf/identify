package com.wotransfer.identify.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.wotransfer.identify.Constants
import com.wotransfer.identify.R
import com.wotransfer.identify.manager.CameraPreviewManager
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.util.showToast
import com.wotransfer.identify.view.util.EnumType
import kotlinx.android.synthetic.main.activity_ocr_view.*
import java.util.*

class OcrReferenceActivity : Activity() {
    private val tag = OcrReferenceActivity::class.java.simpleName

    private var booleanFace: Boolean = false
    private var booleanCard: Boolean = false

    private var licenseId = ""
    private var licenseName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr_view)

        getIntentData()
    }

    private fun getIntentData() {
        booleanCard = intent.getBooleanExtra(Constants.CARD, true)
        booleanFace = intent.getBooleanExtra(Constants.FACE, false)

        intent.getStringExtra(Constants.LICENSE_FILE_NAME)?.let {
            licenseName = it
        } ?: let {
            licenseName = ""
        }
        intent.getStringExtra(Constants.LICENSE_ID)?.let {
            licenseId = it
        } ?: let {
            licenseId = ""
        }


    }

    fun startPhoto(view: View) {
        CameraPreviewManager.getInstance()
            ?.startReferenceFaceOrCard(booleanFace, cardStatus = booleanCard)
            ?.setCameraPreview(camera_p)
            ?.addObserverCameraChange(object : StateObserver {
                //拍摄状态
                override fun stateChange(type: EnumType, state: Boolean, content: String?) {
                    Log.d(tag, "观察者2")
                    btn_intent.visibility = View.GONE
                    btn_intent_repeat.visibility = View.VISIBLE
                }
            })
            ?.addObserverFaceOrCardChange(object : StateObserver {
                //content不为null时，已进行证件认证
                override fun stateChange(type: EnumType, state: Boolean, content: String?) {
                    Log.d(tag, if (type == EnumType.FACE) "开始人脸识别" else "不需要人脸识别")
                    when (type) {
                        EnumType.FACE -> {
                            //需要人脸识别认证
                            startFace()
                        }
                        EnumType.CARD -> {
                            //只进行了证件认证
                        }
                    }
                }
            })

            ?.startTakePhoto()
    }

    //开始人脸识别
    private fun startFace() {
        if (licenseId == "" || licenseName == "") {
            this.showToast(getString(R.string.i_tip_license_1))
            finish()
            return
        }
        val intent = Intent(this@OcrReferenceActivity, KycCameraActivity::class.java)
        intent.putExtra(Constants.LICENSE_ID, licenseId)
        intent.putExtra(Constants.LICENSE_FILE_NAME, licenseName)
        startActivity(intent)
    }

    //重新拍摄
    fun repeatPhoto(view: View) {
        camera_p.updateView()
        btn_intent_repeat.visibility = View.GONE
        btn_intent.visibility = View.VISIBLE
    }

}