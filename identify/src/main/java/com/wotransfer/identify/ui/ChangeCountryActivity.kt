package com.wotransfer.identify.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.wotransfer.identify.R
import kotlinx.android.synthetic.main.activity_change_view.*

class ChangeCountryActivity : Activity() {

    fun back(view: View) {
        this@ChangeCountryActivity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_view)
        any_search_List.setViewAdapter()
    }
}