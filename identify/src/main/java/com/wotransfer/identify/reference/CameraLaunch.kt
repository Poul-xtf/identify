package com.wotransfer.identify.reference

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.wotransfer.identify.Constants
import com.wotransfer.identify.R
import com.wotransfer.identify.ui.IdentifyReferenceActivity
import com.wotransfer.identify.ui.KycCameraActivity
import com.wotransfer.identify.ui.OcrReferenceActivity
import com.wotransfer.identify.util.showToast
import java.io.Serializable
import java.lang.NullPointerException

class CameraLaunch {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        fun register(
            t: Context, appName: String,
            licenseId: String? = null,
            licenseFileName: String? = null,
        ) {
            context = t
            Constants.APP_NAME = appName
            Constants.license_id = licenseId ?: ""
            Constants.license_name = licenseFileName ?: ""
        }
    }

    fun <T> startView(
        t: T, /*vararg params: Any*/
        face: Boolean? = null,
        card: Boolean? = null,
        country: String? = null,
        reference: String? = null,
        model: Serializable? = null,
    ) {
        context?.let {
            when (t) {
                //ocr+人脸
                LaunchType.ALL -> {
                    if (model == null) {
                        it.showToast(it.getString(R.string.i_toast_card))
                        return
                    }
                    val intent = Intent(it, OcrReferenceActivity::class.java)
                    intent.putExtra(Constants.MODEL, model)
                    intent.putExtra(Constants.REFERENCE, reference)
                    intent.putExtra(Constants.FACE, face)
                    intent.putExtra(Constants.CARD, card)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                }
                //ocr
                LaunchType.CAMERA_OCR -> {
                    if (model == null) {
                        it.showToast(it.getString(R.string.i_toast_card))
                        return
                    }
                    val intent = Intent(it, OcrReferenceActivity::class.java)
                    intent.putExtra(Constants.MODEL, model)
                    intent.putExtra(Constants.REFERENCE, reference)
                    intent.putExtra(Constants.FACE, face)
                    intent.putExtra(Constants.CARD, card)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                }
                //人脸
                LaunchType.CAMERA_FACE -> {
                    if (Constants.license_id == "" || Constants.license_name == "") {
                        it.showToast(it.getString(R.string.i_tip_license_1))
                        return
                    }
                    val intent = Intent(it, KycCameraActivity::class.java)
                    intent.putExtra(Constants.COUNTRY_CODE, country)
                    intent.putExtra(Constants.REFERENCE, reference)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                }

                LaunchType.CAMERA_VIEW -> {
                    val intent = Intent(it, IdentifyReferenceActivity::class.java)
                    intent.putExtra(Constants.FACE, face)
                    intent.putExtra(Constants.CARD, card)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intent)
                }
                else -> {
                    val intent = Intent(it, IdentifyReferenceActivity::class.java)
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
        CAMERA_VIEW,
        MY_VIEW,
    }
}