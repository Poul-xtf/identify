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
import org.json.JSONObject

class IdentifyReferenceActivity : BaseKycActivity(), HttpCallBackListener {
    private var booleanFace: String? = null
    private var booleanCard: String? = null
    private var mList = arrayListOf<IdConfigForSdkRO>()
    private var idTypeListBean: IdTypeListBean? = null

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
        val json = JSONObject()
        json.put(Constants.NATIONALITY, "")
        json.put(Constants.TARGET_COUNTRY, "")
        val params = getParams(Constants.CHOOSE_COUNTRY, json.toString())
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
        idTypeListBean = gson.fromJson(content, IdTypeListBean::class.java)
        idTypeListBean?.model?.idConfigForSdkROList?.forEach {
            mList.add(it)
        }
        val referenceMessAdapter = ReferenceMessAdapter(this, mList)
        referenceMessAdapter.setItemListener(object : ReferenceMessAdapter.ItemListener {
            override fun itemBack(position: Int, childPosition: Int) {
                startViewUi(position)
            }
        })
        ep_list.setAdapter(referenceMessAdapter)
        ep_list.setGroupIndicator(null)
        ep_list.setOnGroupCollapseListener {
            referenceMessAdapter.notifyDataSetChanged()
        }
        ep_list.setOnGroupClickListener { _, _, position, _ ->
            startViewUi(position)
            true
        }

    }


    override fun onFiled(path: String, error: String) {
        showToast(error)
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

    private fun startViewUi(position: Int) {
        val intent =
            Intent(this@IdentifyReferenceActivity, OcrReferenceActivity::class.java)
        intent.putExtra(Constants.MODEL, mList[position])
        intent.putExtra(Constants.REFERENCE, idTypeListBean?.model?.reference)
        intent.putExtra(Constants.FACE, booleanFace)
        intent.putExtra(Constants.CARD, booleanCard)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        //取消认证
        val params = getParams(Constants.CHOOSE_COUNTRY)
        startHttpRequest(this, cancel_reference_path, params)
    }
}