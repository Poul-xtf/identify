package com.wotransfer.identify.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.wotransfer.identify.Constants
import com.wotransfer.identify.R
import com.wotransfer.identify.manager.CameraPreviewManager
import com.wotransfer.identify.net.bean.IdConfigForSdkRO
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.util.SpUtil
import com.wotransfer.identify.util.cropImage
import com.wotransfer.identify.util.showToast
import com.wotransfer.identify.view.util.EnumType
import kotlinx.android.synthetic.main.activity_ocr_view.*
import java.io.FileNotFoundException
import java.lang.NullPointerException
import java.util.*

class OcrReferenceActivity : Activity() {
    private val tag = OcrReferenceActivity::class.java.simpleName

    private var booleanFace: Boolean = false
    private var booleanCard: Boolean = false
    private var content: IdConfigForSdkRO? = null
    private var reference: String? = null

    private var licenseId: String? = null
    private var licenseName: String? = null
    private var faceStatus = false
    private var amount = 2
    private var momentLocal = 1

    companion object {
        const val REQUEST_CODE_CHOOSE_IMAGE = 1
        const val REQUEST_CODE_CROP_IMAGE = 2
    }

    private lateinit var cropImageUri: Uri
    private var chooseImage: Boolean = false

    fun finishBack(view: View) {
        this@OcrReferenceActivity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr_view)
        rl_take.background.alpha = 117
        getIntentData()
    }

    private fun getIntentData() {
        booleanCard = intent.getBooleanExtra(Constants.CARD, true)
        booleanFace = intent.getBooleanExtra(Constants.FACE, false)
        reference = intent.getStringExtra(Constants.REFERENCE)
        SpUtil.putString(this, Constants.REFERENCE, reference)

        content = intent.getSerializableExtra(Constants.MODEL) as IdConfigForSdkRO
        SpUtil.putString(this, Constants.COUNTRY_CODE, content?.countryCode)
        //若业务没有传Constants.CARD或者Constants.FACE，则默认model列表中的needOcr、needFace
        SpUtil.putString(this,
            Constants.NEED_OCR,
            if (!booleanCard) "1" else content?.needOcr.toString())
        SpUtil.putString(this,
            Constants.NEED_FACE,
            if (!booleanFace) "1" else content?.needFace.toString())

        amount = content?.idConfigForSdkROList?.size ?: 0

        setTipWay()
    }

    private fun setTipWay() {
        camera_p.updateProgressMax(amount)
        camera_p.updateProgress(momentLocal)
        tv_tip_way.text = String.format(getString(R.string.i_card_way), momentLocal, amount)
    }


    fun startPhoto(view: View) {
        CameraPreviewManager.getInstance()
            ?.startReferenceFaceOrCard(booleanFace, cardStatus = booleanCard)
            ?.setCameraPreview(camera_p)
            ?.addObserverCameraChange(object : StateObserver {
                //拍摄状态
                override fun stateChange(type: EnumType, state: Boolean, content: String?) {
                    Log.d(tag, "观察者2")
                    changeView()
                }
            })
            ?.addObserverFaceOrCardChange(object : StateObserver {
                //content不为null时，已进行证件认证
                override fun stateChange(type: EnumType, state: Boolean, content: String?) {
                    Log.d(tag, if (type == EnumType.FACE) "开始人脸识别" else "不需要人脸识别")
                    when (type) {
                        EnumType.CARD_UP -> {
                            //ocr上传图片
                            if (momentLocal == amount) {
                                if (!booleanFace) {//不需要人脸识别时开始认证
                                    camera_p.referenceImg()
                                } else {
                                    startFace()
                                }
                                return
                            }
                            update()
                            momentLocal += 1
                            setTipWay()
                        }
                        EnumType.CARD -> {
                            //证件认证，则调换认证成功或者失败界面
                            Log.d(Constants.KYC_TAG, "state=$state")
                            launchStatusView(state)
                        }
                        else -> {
//                             EnumType.FACE -> {
//                            //需要人脸识别认证
//                            faceStatus = true
//                        }
                        }
                    }
                }
            })

            ?.startTakePhoto()
    }

    //认证结果
    private fun launchStatusView(state: Boolean) {
        val intent = Intent(this@OcrReferenceActivity, ReferenceResultActivity::class.java)
        intent.putExtra(Constants.RE_STATUS, state)
        startActivity(intent)
        finish()
    }

    //开始人脸识别
    private fun startFace() {
        if (Constants.license_id == "" || Constants.license_name == "") {
            this.showToast(getString(R.string.i_tip_license_1))
            finish()
            return
        }
        val intent = Intent(this@OcrReferenceActivity, KycCameraActivity::class.java)
        intent.putExtra(Constants.COUNTRY_CODE, content?.countryCode)
        startActivity(intent)
        this@OcrReferenceActivity.finish()
    }

    //重新拍摄
    fun repeatPhoto(view: View) {
        update()
    }

    //相册选择
    fun startAlbum(view: View) {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE)
    }

    //确认照片
    fun getPhoto(view: View) {
        setResultUri()
        camera_p.uploadImg(content!!.idConfigForSdkROList[momentLocal - 1].idType)
    }

    private fun setResultUri() {
        if (!chooseImage) {
            return
        }
        camera_p.setResultUrl(cropImageUri.toString())
    }

    private fun update() {
        camera_p.updateView()
        changeView2()
    }

    private fun changeView() {
        rl_cancel_photo.visibility = View.VISIBLE
        rl_get_photo.visibility = View.VISIBLE
        rl_sd_photo.visibility = View.GONE
        iv_album.visibility = View.GONE
        iv_take.visibility = View.GONE
    }

    private fun changeView2() {
        rl_cancel_photo.visibility = View.GONE
        rl_get_photo.visibility = View.GONE
        rl_sd_photo.visibility = View.VISIBLE
        iv_album.visibility = View.VISIBLE
        iv_take.visibility = View.VISIBLE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (intent != null) {
            when (requestCode) {
                REQUEST_CODE_CHOOSE_IMAGE -> if (intent.data != null) {
                    val iconUri = intent.data
                    iconUri?.let {
                        startCropImage(it)
                    } ?: let {
                        throw NullPointerException("uri is null")
                    }
                }
                REQUEST_CODE_CROP_IMAGE -> if (resultCode == RESULT_OK) {
                    chooseImage = true
                    try {
                        val bitmap = BitmapFactory.decodeStream(
                            contentResolver
                                .openInputStream(cropImageUri))
                        camera_p.updateBitmapView(object : StateObserver {
                            override fun stateChange(
                                type: EnumType,
                                state: Boolean,
                                content: String?,
                            ) {
                                changeView()
                            }
                        }, bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun startCropImage(uri: Uri) {
        val intent = cropImage(this, uri)
        cropImageUri = intent.extras!![MediaStore.EXTRA_OUTPUT] as Uri
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE)
    }

    //开关灯光
    fun changeTor(view: View) {
        camera_p.updateTor()
    }

}