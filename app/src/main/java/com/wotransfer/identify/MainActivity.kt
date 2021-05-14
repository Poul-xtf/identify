package com.wotransfer.identify

import android.app.Activity
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import com.example.kyc_camera.impl.CameraPreviewManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : Activity(), Camera.PictureCallback {
    var path = Environment.getExternalStorageDirectory().absolutePath
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startPhoto(view: View) {
        CameraPreviewManager.getInstance()
                ?.setCameraPreview(camera_p)
                ?.setPictureCallback(this)
                ?.startTakePhoto()
    }

    override fun onPictureTaken(p0: ByteArray?, p1: Camera?) {
        Log.d("MainActivity->", "$path")
        val byte = p0
        try {
            val fileOutputStream = FileOutputStream(File("$path/1.png"))
            fileOutputStream.write(byte, 0, byte?.size!!)
            fileOutputStream.close()
            println("Make Picture success,Please find image in$path ")
        } catch (ex: Exception) {
            println("Exception:$ex")
            ex.printStackTrace()

        }
    }
}