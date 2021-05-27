package com.wotransfer.identify.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.wotransfer.identify.Constants
import com.wotransfer.identify.R
import com.wotransfer.identify.ui.KycCameraActivity
import com.wotransfer.identify.ui.OcrReferenceActivity
import com.wotransfer.identify.util.showToast
import java.lang.NullPointerException

class CameraLaunch {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        fun register(t: Context) {
            this.context = t
        }
    }

    fun <T> startView(
        t: T, /*vararg params: Any*/
        face: Boolean? = null,
        card: Boolean? = null,
        licenseId: String? = "",
        licenseFileName: String? = "",
    ) {
        context?.let {
            when (t) {
                LaunchType.ALL -> {
                    val intent = Intent(it, OcrReferenceActivity::class.java)
                    intent.putExtra(Constants.FACE, face)
                    intent.putExtra(Constants.CARD, card)
                    intent.putExtra(Constants.LICENSE_ID, licenseId)
                    intent.putExtra(Constants.LICENSE_FILE_NAME, licenseFileName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                }
                LaunchType.CAMERA_FACE -> {
                    if (licenseId == "" || licenseFileName == "") {
                        it.showToast(it.getString(R.string.i_tip_license_1))
                        return
                    }
                    val intent = Intent(it, KycCameraActivity::class.java)
                    intent.putExtra(Constants.LICENSE_ID, licenseId)
                    intent.putExtra(Constants.LICENSE_FILE_NAME, licenseFileName)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                }
                else -> {
                    val intent = Intent(it, OcrReferenceActivity::class.java)
                    intent.putExtra(Constants.FACE, face)
                    intent.putExtra(Constants.CARD, card)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                }
            }
        } ?: let {
            throw NullPointerException("CameraLaunch is register")
        }


    }


    enum class LaunchType {
        ALL,
        CAMERA_OCR,
        CAMERA_FACE,
    }
}