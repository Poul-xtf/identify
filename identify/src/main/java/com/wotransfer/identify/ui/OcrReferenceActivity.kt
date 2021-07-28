package com.wotransfer.identify.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.wotransfer.identify.Constants
import com.wotransfer.identify.Constants.Companion.KYC_TAG
import com.wotransfer.identify.R
import com.wotransfer.identify.databinding.ActivityOcrViewBinding
import com.wotransfer.identify.manager.CameraPreviewManager
import com.wotransfer.identify.net.bean.IdConfigForSdkRO
import com.wotransfer.identify.observeInterface.StateObserver
import com.wotransfer.identify.util.SpUtil
import com.wotransfer.identify.util.cropImage
import com.wotransfer.identify.util.showToast
import com.wotransfer.identify.view.util.EnumType
import java.io.FileNotFoundException
import java.util.*

class OcrReferenceActivity : BaseKycActivity<ActivityOcrViewBinding>() {
    private var booleanFace: Int? = -1
    private var booleanCard: Int? = -1
    private var content: IdConfigForSdkRO? = null
    private var reference: String? = null
    private var amount = 2
    private var momentLocal = 1
    private var cameraPreview: CameraPreviewManager? = null
    private var alwaysReference: Boolean = false

    companion object {
        const val REQUEST_CODE_CHOOSE_IMAGE = 1
        const val REQUEST_CODE_CROP_IMAGE = 2
        const val REQUEST_CODE = 0
        const val REQUEST_CODE_SUCCESS = -1
        const val STATUS_TASK_FALSE = 1
    }

    private lateinit var cropImageUri: Uri
    private var chooseImage: Boolean = false

    fun finishBack(view: View) {
        finished()
    }

    override fun initView() {
        getIntentData()
    }

    private fun getIntentData() {
        reference = intent.getStringExtra(Constants.REFERENCE)
        content = intent.getSerializableExtra(Constants.MODEL)?.let {
            it as IdConfigForSdkRO
        }
        val card = intent.getIntExtra(Constants.CARD, Constants.DEFAULT_CODE)
        val face = intent.getIntExtra(Constants.FACE, Constants.DEFAULT_CODE)
        booleanCard = if (card == Constants.DEFAULT_CODE) {
            content?.needOcr
        } else {
            card
        }
        booleanFace = if (face == Constants.DEFAULT_CODE) {
            content?.needFace
        } else {
            face
        }
        amount = content?.idConfigForSdkROList?.size ?: 0
        SpUtil.putString(this, Constants.REFERENCE, reference)
        SpUtil.putString(this, Constants.COUNTRY_CODE, content?.countryCode)
        SpUtil.putString(this, Constants.NEED_OCR, booleanCard.toString())
        SpUtil.putString(this, Constants.NEED_FACE, booleanFace.toString())
        setTipWay()
        initData()
    }

    private fun initData() {
        cameraPreview = CameraPreviewManager.getInstance()
            ?.startReferenceFaceOrCard(
                booleanFace == Constants.OPEN_FACE,
                cardStatus = booleanCard == Constants.OPEN_CARD
            )
            ?.setCameraPreview(binding.cameraP)
            ?.addObserverCameraChange(object : StateObserver {
                override fun stateChange(type: EnumType, state: Boolean, content: String?) {
                    changeView()
                }
            })?.addObserverFaceOrCardChange(object : StateObserver {
                override fun stateChange(type: EnumType, state: Boolean, content: String?) {
                    when (type) {
                        EnumType.CARD_UP -> {
                            if (momentLocal == amount) {
                                if (booleanFace == Constants.CLOSE_FACE) {
                                    binding.cameraP.referenceImg()
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
                            Log.d(KYC_TAG, "state=$state")
                            launchStatusView(state, content)
                        }
                        else -> {
                        }
                    }
                }
            })
    }

    private fun setTipWay() {
        content?.idConfigForSdkROList?.let {
            binding.tvTipTitle.text = it[momentLocal - 1].idName
            binding.cameraP.updateCropBView(it[momentLocal - 1].borderUrl)
        }
        binding.cameraP.updateProgressMax(amount)
        binding.cameraP.updateProgress(momentLocal)
        binding.tvTipWay.text = String.format(getString(R.string.i_card_way), momentLocal, amount)
    }

    //拍照
    fun startPhoto(view: View) {
        cameraPreview?.startTakePhoto()
    }

    //认证结果(无人脸)
    private fun launchStatusView(state: Boolean, content: String?) {
        alwaysReference = true
        val intent = Intent(this@OcrReferenceActivity, ReferenceResultActivity::class.java)
        intent.putExtra(Constants.RE_STATUS, state)
        startActivity(intent)
        if (state) {
            setResult(-1, Intent().putExtra(Constants.OCR_DATA, content))
        }
        finished()
    }

    //开始人脸识别
    private fun startFace() {
        if (Constants.LICENSE_ID == "" || Constants.LICENSE_NAME == "") {
            this.showToast(getString(R.string.i_tip_license_1))
            finished()
            return
        }
        val intent = Intent(this@OcrReferenceActivity, KycCameraActivity::class.java)
        intent.putExtra(Constants.COUNTRY_CODE, content?.countryCode)
        intent.putExtra(Constants.REFERENCE, reference)
        startActivityForResult(intent, REQUEST_CODE)
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
        Log.i(KYC_TAG,"momentLocal:$momentLocal")
        content?.idConfigForSdkROList?.let {
            binding.cameraP.uploadImg(it[momentLocal - 1].idType)
        } ?: showToast(getString(R.string.i_toast_idType))
    }

    private fun setResultUri() {
        if (!chooseImage) {
            return
        }
        binding.cameraP.setResultUrl(cropImageUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUEST_CODE_CHOOSE_IMAGE -> if (intent != null) {
                if (intent.data != null) {
                    val iconUri = intent.data
                    iconUri?.let {
                        startCropImage(it)
                    }
                }
            }
            REQUEST_CODE_CROP_IMAGE -> if (resultCode == RESULT_OK) {
                chooseImage = true
                try {
                    val bitmap = BitmapFactory.decodeStream(
                        contentResolver
                            .openInputStream(cropImageUri)
                    )
                    cameraPreview?.updateBitmapView(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
            REQUEST_CODE -> {
                //人脸识别回调
                if (resultCode == REQUEST_CODE_SUCCESS) {
                    //人脸认证成功
                    intent?.let {
                        val ocrData = it.getStringExtra(Constants.OCR_DATA)
                        setResult(-1, Intent().putExtra(Constants.OCR_DATA, ocrData))
                    }
                }
                finished()
            }
            else -> {
            }
        }
    }

    private fun startCropImage(uri: Uri) {
        val intent = cropImage(this, uri)
        cropImageUri = intent.extras!![MediaStore.EXTRA_OUTPUT] as Uri
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE)
    }

    private fun update() {
        binding.cameraP.updateView()
        changeView2()
    }

    private fun changeView() {
        binding.rlCancelPhoto.visibility = View.VISIBLE
        binding.rlGetPhoto.visibility = View.VISIBLE
        binding.rlSdPhoto.visibility = View.GONE
        binding.ivAlbum.visibility = View.GONE
        binding.ivTake.visibility = View.GONE
    }

    private fun changeView2() {
        binding.rlCancelPhoto.visibility = View.GONE
        binding.rlGetPhoto.visibility = View.GONE
        binding.rlSdPhoto.visibility = View.VISIBLE
        binding.ivAlbum.visibility = View.VISIBLE
        binding.ivTake.visibility = View.VISIBLE
    }

    //开关灯光
    fun changeTor(view: View) {
        binding.cameraP.updateTor()
    }

    private fun finished() {
        this@OcrReferenceActivity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!alwaysReference && booleanFace == Constants.CLOSE_FACE) {
            //未进行认证并且不需要进行人脸认证，则退出界面时取消认证
            cameraPreview?.cancelReference()
        }
    }

}