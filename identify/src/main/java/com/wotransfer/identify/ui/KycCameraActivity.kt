package com.wotransfer.identify.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import com.baidu.idl.face.platform.FaceEnvironment
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.LivenessTypeEnum
import com.baidu.idl.face.platform.listener.IInitCallback
import com.baidu.idl.face.platform.ui.FaceLivenessActivity
import com.baidu.idl.face.platform.utils.Base64Utils
import com.wotransfer.identify.Constants
import com.wotransfer.identify.Constants.Companion.QUALITY_NORMAL
import com.wotransfer.identify.R
import com.wotransfer.identify.faceutil.manager.QualityConfigManager
import com.wotransfer.identify.faceutil.model.QualityConfig
import com.wotransfer.identify.net.*
import com.wotransfer.identify.util.saveBitmap
import com.wotransfer.identify.util.showToast
import kotlinx.android.synthetic.main.activity_kyc_view.*
import java.io.File
import java.lang.NullPointerException
import java.util.*

class KycCameraActivity : BaseKycActivity(), HttpCallBackListener {
    // 活体随机开关
    private var isLiveNessRandom = true

    // 语音播报开关
    private var isOpenSound = true
    private var liveNessList: List<LivenessTypeEnum> = ArrayList()
    private var country: String = ""
    private var reference: String = ""
    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kyc_view)
        getIntentData()
        init()
    }

    private fun getIntentData() {
        country = intent.getStringExtra(Constants.COUNTRY_CODE)
    }

    private fun init() {
        val success = setFaceConfig()
        if (!success) {
            this.showToast(getString(R.string.i_face_error))
            return
        }

        FaceSDKManager.getInstance()
            .initialize(this, Constants.license_id, Constants.license_name, object : IInitCallback {
                override fun initSuccess() {
                    runOnUiThread {
                        Log.e(Constants.KYC_TAG, "初始化成功")
                        startActivityForResult(Intent(this@KycCameraActivity,
                            FaceLivenessActivity::class.java), 0)
                    }
                }

                override fun initFailure(errCode: Int, errMsg: String) {
                    runOnUiThread {
                        Log.e(Constants.KYC_TAG, "初始化失败 = $errCode $errMsg")
                    }
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            1 -> {
                val bitmapBase64 = data?.getStringExtra("bitmap")
                val bytes = Base64Utils.decode(bitmapBase64, Base64Utils.NO_WRAP)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                iv_tess.setImageBitmap(bitmap)
                //存在本地获取地址
                file = saveBitmap(bitmap)
                //上传图片、并认证
                uploadImg()
            }
        }
    }

    /**
     * upload face image
     */
    private fun uploadImg() {
        if (reference == "") {
            throw NullPointerException("reference is null")
        }
        file?.let {
            val params = getParams(Constants.APP_NAME,
                country,
                0,
                "",
                1,
                reference,
                it)
            startHttpRequest(this, upload_identity_path, params)
        } ?: let {
            throw NullPointerException("save face is failed,file is null")
        }

    }

    /**
     * reference face image
     */
    private fun referenceImg() {
        val params = getReParams(Constants.APP_NAME, reference)
        startHttpRequest(this, reference_path, params)
    }

    override fun onSuccess(path: String, content: String) {
        when (path) {
            upload_identity_path -> {
                referenceImg()
            }
            reference_path -> {
                startActivity(Intent(this, ReferenceResultActivity::class.java))
                finish()
            }

        }
    }

    override fun onFiled() {
    }

    override fun complete() {
    }

    /**
     * 参数配置方法
     */
    private fun setFaceConfig(): Boolean {
        val config = FaceSDKManager.getInstance().faceConfig
        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
        // 质量等级（0：正常、1：宽松、2：严格、3：自定义）
        // 获取保存的质量等级
//        val util = SharedPreferencesUtil(this)
//        var qualityLevel = util.getSharedPreference(KEY_QUALITY_LEVEL_SAVE, -1) as Int
//        if (qualityLevel == -1) {
//            qualityLevel = QUALITY_NORMAL
//        }
        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        val manager: QualityConfigManager = QualityConfigManager.instance!!
        manager.readQualityFile(applicationContext, /*qualityLevel*/QUALITY_NORMAL)
        val qualityConfig: QualityConfig = manager.config ?: return false
        // 设置模糊度阈值
        config.blurnessValue = qualityConfig.blur
        // 设置最小光照阈值（范围0-255）
        config.brightnessValue = qualityConfig.minIllum
        // 设置最大光照阈值（范围0-255）
        config.brightnessMaxValue = qualityConfig.maxIllum
        // 设置左眼遮挡阈值
        config.occlusionLeftEyeValue = qualityConfig.leftEyeOcclusion
        // 设置右眼遮挡阈值
        config.occlusionRightEyeValue = qualityConfig.rightEyeOcclusion
        // 设置鼻子遮挡阈值
        config.occlusionNoseValue = qualityConfig.noseOcclusion
        // 设置嘴巴遮挡阈值
        config.occlusionMouthValue = qualityConfig.mouseOcclusion
        // 设置左脸颊遮挡阈值
        config.occlusionLeftContourValue = qualityConfig.leftContourOcclusion
        // 设置右脸颊遮挡阈值
        config.occlusionRightContourValue = qualityConfig.rightContourOcclusion
        // 设置下巴遮挡阈值
        config.occlusionChinValue = qualityConfig.chinOcclusion
        // 设置人脸姿态角阈值
        config.headPitchValue = qualityConfig.pitch
        config.headYawValue = qualityConfig.yaw
        config.headRollValue = qualityConfig.roll
        // 设置可检测的最小人脸阈值
        config.minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE
        // 设置可检测到人脸的阈值
        config.notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD
        // 设置闭眼阈值
        config.eyeClosedValue = FaceEnvironment.VALUE_CLOSE_EYES
        // 设置图片缓存数量
        config.cacheImageNum = FaceEnvironment.VALUE_CACHE_IMAGE_NUM
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.livenessTypeList = liveNessList
        // 设置动作活体是否随机
        config.isLivenessRandom = isLiveNessRandom
        // 设置开启提示音
        config.isSound = isOpenSound
        // 原图缩放系数
        config.scale = FaceEnvironment.VALUE_SCALE
        // 抠图宽高的设定，为了保证好的抠图效果，建议高宽比是4：3
        config.cropHeight = FaceEnvironment.VALUE_CROP_HEIGHT
        config.cropWidth = FaceEnvironment.VALUE_CROP_WIDTH
        // 抠图人脸框与背景比例
        config.enlargeRatio = FaceEnvironment.VALUE_CROP_ENLARGERATIO
        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
        config.secType = FaceEnvironment.VALUE_SEC_TYPE
        // 检测超时设置
        config.timeDetectModule = FaceEnvironment.TIME_DETECT_MODULE
        // 检测框远近比率
        config.faceFarRatio = FaceEnvironment.VALUE_FAR_RATIO
        config.faceClosedRatio = FaceEnvironment.VALUE_CLOSED_RATIO
        FaceSDKManager.getInstance().faceConfig = config
        return true
    }


}