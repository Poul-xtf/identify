package com.wotransfer.identify.util

import android.content.Context
import android.widget.Toast


var toast: Toast? = null
fun Context.showToast(tip: String) {
    toast?.let {
        it.setText(tip)
        it.show()
    } ?: let {
        toast = Toast.makeText(this, tip, Toast.LENGTH_SHORT)
        toast?.show()
    }
}