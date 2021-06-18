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
        var mContext: Context? = null
        fun register(
            context: Context, appName: String,
            licenseId: String? = null,
            licenseFileName: String? = null,
        ) {
            mContext = context
            Constants.APP_NAME = appName
            Constants.LICENSE_ID = licenseId ?: ""
            Constants.LICENSE_NAME = licenseFileName ?: ""
        }
    }

    fun startView(
        type: LaunchType,
        face: Int? = null,
        card: Int? = null,
        country: String? = null,
        reference: String? = null,
        model: Serializable? = null,
    ) {
        mContext?.let {
            when (type) {
                LaunchType.ALL -> {
                    if (model == null) {
                        it.showToast(it.getString(R.string.i_tip_license_3))
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
                LaunchType.CAMERA_OCR -> {
                    if (model == null) {
                        it.showToast(it.getString(R.string.i_tip_license_3))
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
                LaunchType.CAMERA_FACE -> {
                    if (Constants.LICENSE_ID == "" || Constants.LICENSE_NAME == "") {
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
        MY_VIEW
    }
}