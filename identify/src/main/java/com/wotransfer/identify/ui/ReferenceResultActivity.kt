package com.wotransfer.identify.ui

import android.view.View
import com.wotransfer.identify.Constants
import com.wotransfer.identify.R
import com.wotransfer.identify.databinding.ActivityResultViewBinding
import com.wotransfer.identify.util.getDrawable

class ReferenceResultActivity : BaseKycActivity<ActivityResultViewBinding>() {

    lateinit var binding: ActivityResultViewBinding
    override fun getContentView(): ActivityResultViewBinding {
        binding = ActivityResultViewBinding.inflate(layoutInflater)
        return binding
    }

    override fun initView() {
        val booleanExtra = intent.getBooleanExtra(Constants.RE_STATUS, false)
        if (!booleanExtra) {
            binding.ivBackSuccess.getDrawable(this, R.drawable.shape_re_failed)
            binding.ivReStatus2.visibility = View.VISIBLE
            binding.ivReStatus.visibility = View.GONE
            binding.tvReTip.text = getString(R.string.i_text_identify_failed)
            binding.tvReTip2.text = getString(R.string.i_text_identify_finish_failed)
        }
    }

    fun finishBack(view: View) {
        this@ReferenceResultActivity.finish()
    }
}