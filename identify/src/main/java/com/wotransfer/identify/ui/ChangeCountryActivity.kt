package com.wotransfer.identify.ui

import android.content.Intent
import android.view.View
import com.wotransfer.identify.Constants
import com.wotransfer.identify.databinding.ActivityChangeViewBinding

class ChangeCountryActivity : BaseKycActivity<ActivityChangeViewBinding>() {
    fun back(view: View) {
        this@ChangeCountryActivity.finish()
    }

    override fun initView() {
        binding.anySearchList.setViewAdapter { countryCode: String, countryName: String ->
            val intent = Intent()
            intent.putExtra(Constants.COUNTRY_CODE, countryCode)
            intent.putExtra(Constants.COUNTRY_NAME, countryName)
            setResult(resultBackCode, intent)
            finish()
        }
    }
}