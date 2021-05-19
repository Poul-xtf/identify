package com.wotransfer.identify

import android.app.Activity
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.kyc_camera.impl.BaseManager
import com.example.kyc_camera.impl.CameraPreviewManager
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
                ?.startTakePhoto()
    }

    fun getPhoto(view: View) {
        CameraPreviewManager.getInstance()?.statusMap?.forEach { (t, u )->
            Log.d("xtf->","$t---$u")
        }
    }
}