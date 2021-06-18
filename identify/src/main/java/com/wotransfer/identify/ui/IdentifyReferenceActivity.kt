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
import kotlinx.android.synthetic.main.activity_kyc_view.*

class IdentifyReferenceActivity : BaseKycActivity(), HttpCallBackListener {
    private var booleanFace: String? = null
    private var booleanCard: String? = null
    private var mList = arrayListOf<IdConfigForSdkRO>()

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
        mList.clear()
        val params = getParams(Constants.CHOOSE_COUNTRY)
        startHttpRequest(this, identity_list_path, params)
    }

    private fun initView() {
        booleanCard = intent.getStringExtra(Constants.CARD)
        booleanFace = intent.getStringExtra(Constants.FACE)
        setText()
    }

    private fun setText() {
        val string = getString(R.string.i_text_reference_country, Constants.CHOOSE_COUNTRY_NAME)
        tv_re_country.htmlFormat(string)
    }

    override fun onSuccess(path: String, content: String) {
        val gson = Gson()
//        val content = getJson("idConfig.json", this)
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
                intent.putExtra(Constants.FACE, booleanFace)
                intent.putExtra(Constants.CARD, booleanCard)
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
            intent.putExtra(Constants.FACE, booleanFace)
            intent.putExtra(Constants.CARD, booleanCard)
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
        startActivityForResult(Intent(this, ChangeCountryActivity::class.java), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            resultBackCode -> {
                Constants.CHOOSE_COUNTRY =
                    data?.getStringExtra(Constants.COUNTRY_CODE) ?: Constants.CHOOSE_COUNTRY
                Constants.CHOOSE_COUNTRY_NAME =
                    data?.getStringExtra(Constants.COUNTRY_NAME) ?: Constants.CHOOSE_COUNTRY_NAME
                setText()
                getNetData()
            }
        }
    }
}