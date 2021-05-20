package com.wotransfer.identify

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.kyc_camera.faceutil.observeUtil.StateObserver
import com.example.kyc_camera.impl.BaseManager
import com.example.kyc_camera.impl.CameraPreviewManager
import com.example.kyc_camera.ui.KycCameraActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer


@RequiresApi(Build.VERSION_CODES.KITKAT)
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startPhoto(view: View) {
        CameraPreviewManager.getInstance()
                ?.startReferenceFace(true)
                ?.startReferenceCard(false)
                ?.setCameraPreview(camera_p)
//                ?.setPictureCallback(this)
                ?.addObserverFaceChange(object : StateObserver {
                    override fun stateChange(state: Boolean) {
                        Log.d("xtf->", if (state) "11" else "22")
                        //观察是否需要人脸识别认证
                        when (state) {
                            true -> {
                                startActivity(Intent(this@MainActivity, KycCameraActivity::class.java))
                            }
                        }
                    }
                })
                ?.startTakePhoto()
    }

    fun getPhoto(view: View) {
        CameraPreviewManager.getInstance()?.statusMap?.forEach { (t, u) ->
            Log.d("xtf->", "$t---$u")
        }
    }
}