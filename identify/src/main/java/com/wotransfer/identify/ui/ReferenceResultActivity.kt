package com.wotransfer.identify.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wotransfer.identify.Constants
import com.wotransfer.identify.R
import com.wotransfer.identify.util.getDrawable
import kotlinx.android.synthetic.main.activity_result_view.*

class ReferenceResultActivity : BaseKycActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_view)
        initView()
    }

    private fun initView() {
        val booleanExtra = intent.getBooleanExtra(Constants.RE_STATUS, false)
        if (!booleanExtra) {
            iv_back_success.getDrawable(this, R.drawable.shape_re_failed)
            iv_re_status_2.visibility = View.VISIBLE
            iv_re_status.visibility = View.GONE
            tv_re_tip.text = getString(R.string.i_text_identify_failed)
            tv_re_tip2.text = getString(R.string.i_text_identify_finish_failed)
        }

    }

    fun finishBack(view: View) {
        this@ReferenceResultActivity.finish()
    }
}