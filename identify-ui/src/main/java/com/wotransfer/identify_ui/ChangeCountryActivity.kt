package com.wotransfer.identify_ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_change_view.*

class ChangeCountryActivity : Activity() {
    var thisData = arrayListOf<String>()


    fun back(view: View) {
        this@ChangeCountryActivity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_view)
        thisData.add("12213")
        thisData.add("1221")
        thisData.add("12214")
        thisData.add("1221")
        thisData.add("1221")
        thisData.add("1221")
        thisData.add("12213")
        thisData.add("1221")
        thisData.add("12214")
        thisData.add("1221")
        thisData.add("1221")
        thisData.add("1221")
        thisData.add("12213")
        thisData.add("1221")
        thisData.add("12214")
        thisData.add("1221")
        thisData.add("1221")
        thisData.add("1221")
        any_search_List.setListData(thisData)
    }
}