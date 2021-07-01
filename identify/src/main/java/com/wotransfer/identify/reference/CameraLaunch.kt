package com.wotransfer.identify.reference

import android.annotation.SuppressLint
import android.app.Activity
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
        context: Activity,
        type: LaunchType,
        face: Int? = null,
        card: Int? = null,
        country: String? = null,
        reference: String? = null,
        model: Serializable? = null,
        requestCode: Int,
    ) {
        mContext?.let {
//            requestCode?.let { _ ->
//                SpUtil.putString(it, Constants.REQUEST_CODE, requestCode.toString())
//            }
            when (type) {
                LaunchType.ALL -> {
                    if (model == null) {
                        it.showToast(it.getString(R.string.i_tip_license_3))
                        return
                    }
                    if (reference == "" || reference == null) {
                        it.showToast(it.getString(R.string.i_tip_license_4))
                        return
                    }
                    val intent = Intent(context, OcrReferenceActivity::class.java)
                    intent.putExtra(Constants.MODEL, model)
                    intent.putExtra(Constants.REFERENCE, reference)
                    intent.putExtra(Constants.FACE, face)
                    intent.putExtra(Constants.CARD, card)
                    context.startActivityForResult(intent, requestCode)
                }
                LaunchType.CAMERA_OCR -> {
                    if (model == null) {
                        it.showToast(it.getString(R.string.i_tip_license_3))
                        return
                    }
                    if (reference == "" || reference == null) {
                        it.showToast(it.getString(R.string.i_tip_license_4))
                        return
                    }
                    val intent = Intent(context, OcrReferenceActivity::class.java)
                    intent.putExtra(Constants.MODEL, model)
                    intent.putExtra(Constants.REFERENCE, reference)
                    intent.putExtra(Constants.FACE, face)
                    intent.putExtra(Constants.CARD, card)
                    context.startActivityForResult(intent, requestCode)
                }
                LaunchType.CAMERA_FACE -> {
                    if (Constants.LICENSE_ID == "" || Constants.LICENSE_NAME == "") {
                        it.showToast(it.getString(R.string.i_tip_license_1))
                        return
                    }
                    if (reference == "" || reference == null) {
                        it.showToast(it.getString(R.string.i_tip_license_4))
                        return
                    }
                    val intent = Intent(context, KycCameraActivity::class.java)
                    intent.putExtra(Constants.COUNTRY_CODE, country)
                    intent.putExtra(Constants.REFERENCE, reference)
                    context.startActivityForResult(intent, requestCode)
                }

                LaunchType.CAMERA_VIEW -> {
                    val intent = Intent(it, IdentifyReferenceActivity::class.java)
                    intent.putExtra(Constants.FACE, face)
                    intent.putExtra(Constants.CARD, card)
                    context.startActivityForResult(intent, requestCode)
                }
                else -> {
                    val intent = Intent(it, IdentifyReferenceActivity::class.java)
                    context.startActivityForResult(intent, requestCode)
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