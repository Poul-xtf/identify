package com.wotransfer.identify.util

import android.content.Context
import android.widget.Toast


var toast: Toast? = null
fun <T> T.showToast(tip: String) {
    toast?.run {
        cancel()
        null
        toast = Toast.makeText(this@showToast as Context, tip, Toast.LENGTH_SHORT)
        toast?.show()
    }
}