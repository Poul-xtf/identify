package com.wotransfer.identify_ui

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.wotransfer.identify.Constants
import com.wotransfer.identify.net.*
import com.wotransfer.identify.util.getJson
import com.wotransfer.identify_ui.adapter.ReferenceMessAdapter
import com.wotransfer.identify_ui.dialog.TipDialogFragment
import com.wotransfer.identify_ui.enum.ReferenceEnum
import com.wotransfer.identify_ui.reference.bean.IdConfigForSdkRO
import com.wotransfer.identify_ui.reference.bean.IdTypeListBean
import com.wotransfer.identify_ui.util.htmlFormat
import kotlinx.android.synthetic.main.activity_identity_view.*
import org.json.JSONObject

class IdentifyReferenceActivity : AppCompatActivity(), HttpCallBackListener {
    //    private var mList = arrayListOf<IdConfigForSdkRO>()
    private var mList: List<IdConfigForSdkRO>? = null
    private var referenceMessAdapter: ReferenceMessAdapter? = null

    private var countryName = "美国"

    private var tipDialogFragment: TipDialogFragment? = null
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

    fun back(view: View) {
        this@IdentifyReferenceActivity.finish()
    }

    override fun onSuccess(temp: String) {
//        var mList = mList

        val gson = Gson()
        val strData = getJson("idConfig.json", this)
        val idTypeListBean = gson.fromJson(strData, IdTypeListBean::class.java)
//        idTypeListBean.model.idConfigForSdkROList.forEach {
//            mList!!.add(it)
//        }
        mList = idTypeListBean.model.idConfigForSdkROList
//        referenceMessAdapter?.notifyDataSetChanged() ?: let {
        val referenceMessAdapter = ReferenceMessAdapter(this, mList!!)
//        }
        referenceMessAdapter?.setItemListener(object : ReferenceMessAdapter.ItemListener {
            override fun itemBack() {
//                tipDialogFragment = tipDialogFragment ?: TipDialogFragment()
//                tipDialogFragment?.show(supportFragmentManager, "paizhao")
            }
        })
        ep_list.setAdapter(referenceMessAdapter)
        ep_list.setOnGroupCollapseListener {
            referenceMessAdapter?.notifyDataSetChanged()
        }
    }


    override fun onFiled() {
    }

    override fun complete() {
    }
}