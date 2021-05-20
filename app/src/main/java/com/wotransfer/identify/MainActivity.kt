package com.wotransfer.identify

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.kyc_camera.faceutil.observeInterface.StateObserver
import com.example.kyc_camera.manager.CameraPreviewManager
import com.example.kyc_camera.ui.KycCameraActivity
import com.example.kyc_camera.view.util.EnumType
import kotlinx.android.synthetic.main.activity_main.*


@RequiresApi(Build.VERSION_CODES.KITKAT)
class MainActivity : Activity() {
    private val tag = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startPhoto(view: View) {
        CameraPreviewManager.getInstance()
                ?.startReferenceFace(true)
                ?.startReferenceCard(false)
                ?.setCameraPreview(camera_p)
                ?.addObserverFaceChange(object : StateObserver {
                    override fun stateChange(type: EnumType, state: Boolean) {
                        Log.d(tag, if (type == EnumType.FACE) "开始人脸识别" else "不需要人脸识别")
                        when (type) {
                            EnumType.FACE -> {
                                //需要人脸识别认证
                                val intent = Intent(this@MainActivity, KycCameraActivity::class.java)
                                intent.putExtra("licenseId", "WotransferIdentify-face-android")
                                intent.putExtra("licenseFileName", "WotransferIdentify-face-android")
                                startActivity(intent)
                            }
                            EnumType.CARD -> {
                                //只进行了证件认证
                            }
                        }
                    }
                })
                ?.startTakePhoto()
    }

    fun getPhoto(view: View) {
        CameraPreviewManager.getInstance()?.statusMap?.forEach { (t, u) ->
            Log.d(tag, "$t---$u")
        }
    }
}