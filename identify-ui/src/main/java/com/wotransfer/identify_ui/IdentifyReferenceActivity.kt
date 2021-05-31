package com.wotransfer.identify_ui

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wotransfer.identify_ui.adapter.ReferenceMessAdapter
import com.wotransfer.identify_ui.dialog.TipDialogFragment
import com.wotransfer.identify_ui.enum.ReferenceEnum
import kotlinx.android.synthetic.main.activity_identity_view.*

class IdentifyReferenceActivity : AppCompatActivity() {
    private var mList = arrayListOf<TestData>()

    private var countryName = "美国"

    private var tipDialogFragment: TipDialogFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identity_view)
        initView()

    }

    private fun initView() {
        val string = getString(R.string.i_text_reference_country, countryName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_re_country.text = Html.fromHtml(string, 0)
        }
        mList.add(TestData(ReferenceEnum.PASSPORT.value(),
            TestData.ChildBean(getString(R.string.i_text_passport))))
        mList.add(TestData(ReferenceEnum.DRIVER.value(),
            TestData.ChildBean(getString(R.string.i_text_driver))))
        mList.add(TestData(ReferenceEnum.IDENTIFY.value(),
            TestData.ChildBean(getString(R.string.i_text_identity))))
        mList.add(TestData(ReferenceEnum.VISA.value(),
            TestData.ChildBean(getString(R.string.i_text_visa))))

        val referenceMessAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ReferenceMessAdapter(this, mList)
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        referenceMessAdapter.setItemListener(object : ReferenceMessAdapter.ItemListener {
            override fun itemBack() {
                tipDialogFragment = tipDialogFragment ?: TipDialogFragment()
                tipDialogFragment?.show(supportFragmentManager, "paizhao")
            }
        })
        ep_list.setAdapter(referenceMessAdapter)
        ep_list.setOnGroupCollapseListener {
            referenceMessAdapter.notifyDataSetChanged()
        }
    }

    fun back(view: View) {
        finish()
    }

    class TestData(var type: String?, var dataBean: ChildBean?) {
        class ChildBean(var name: String?)
    }
}