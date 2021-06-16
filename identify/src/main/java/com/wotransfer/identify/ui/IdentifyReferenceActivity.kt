package com.wotransfer.identify.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.wotransfer.identify.Constants
import com.wotransfer.identify.R
import com.wotransfer.identify.net.*
import com.wotransfer.identify.net.bean.IdConfigForSdkRO
import com.wotransfer.identify.net.bean.IdTypeListBean
import com.wotransfer.identify.ui.adapter.ReferenceMessAdapter
import com.wotransfer.identify.util.htmlFormat
import com.wotransfer.identify.util.showToast
import kotlinx.android.synthetic.main.activity_identity_view.*

class IdentifyReferenceActivity : BaseKycActivity(), HttpCallBackListener {
    private var mList = arrayListOf<IdConfigForSdkRO>()
    private var countryName = "日本"

    fun back(view: View) {
        this@IdentifyReferenceActivity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identity_view)
        initView()
        getNetData()
    }

    private fun getNetData() {
        val params = getParams(Constants.APP_NAME, "JPN")
        startHttpRequest(this, identity_list_path, params)
    }

    private fun initView() {

        val string = getString(R.string.i_text_reference_country, countryName)
        tv_re_country.htmlFormat(string)
    }


    override fun onSuccess(path: String, content: String) {
        val gson = Gson()
//        val strData = getJson("idConfig.json", this)
        val idTypeListBean = gson.fromJson(content, IdTypeListBean::class.java)
        idTypeListBean.model.idConfigForSdkROList.forEach {
            mList.add(it)
        }
        val referenceMessAdapter = ReferenceMessAdapter(this, mList)
        referenceMessAdapter.setItemListener(object : ReferenceMessAdapter.ItemListener {
            override fun itemBack(position: Int, childPosition: Int) {
                val intent =
                    Intent(this@IdentifyReferenceActivity, OcrReferenceActivity::class.java)
                intent.putExtra(Constants.MODEL, mList[position])
                intent.putExtra(Constants.REFERENCE, idTypeListBean.model.reference)
                startActivity(intent)
            }
        })
        ep_list.setAdapter(referenceMessAdapter)
        ep_list.setGroupIndicator(null)
        ep_list.setOnGroupCollapseListener {
            referenceMessAdapter.notifyDataSetChanged()
        }
        ep_list.setOnGroupClickListener { _, _, position, _ ->
            val intent =
                Intent(this@IdentifyReferenceActivity, OcrReferenceActivity::class.java)
            intent.putExtra(Constants.MODEL, mList[position])
            intent.putExtra(Constants.REFERENCE, idTypeListBean.model.reference)
            startActivity(intent)
            true
        }

    }


    override fun onFiled() {
        showToast(getString(R.string.i_toast_card_failed))
    }

    override fun complete() {
    }

    fun changeCountry(view: View) {
        startActivity(Intent(this, ChangeCountryActivity::class.java))
    }
}