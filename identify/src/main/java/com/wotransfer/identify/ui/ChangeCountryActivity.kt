package com.wotransfer.identify.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wotransfer.identify.Constants
import com.wotransfer.identify.R
import kotlinx.android.synthetic.main.activity_change_view.*

class ChangeCountryActivity : BaseKycActivity() {

    fun back(view: View) {
        this@ChangeCountryActivity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_view)
        any_search_List.setViewAdapter { countryCode: String, countryName: String ->
            val intent = Intent()
            intent.putExtra(Constants.COUNTRY_CODE, countryCode)
            intent.putExtra(Constants.COUNTRY_NAME, countryName)
            setResult(resultBackCode, intent)
            finish()
        }
    }
}