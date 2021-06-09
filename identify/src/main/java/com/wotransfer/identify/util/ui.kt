package com.wotransfer.identify.util

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView

fun TextView.htmlFormat(string: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(string, 0)
    }
}

fun <T : View> T.getDrawable(context: Context, view: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.background = context.resources.getDrawable(view, null)
    }
}