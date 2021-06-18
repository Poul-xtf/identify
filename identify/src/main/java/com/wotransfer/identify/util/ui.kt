package com.wotransfer.identify.util

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.wotransfer.identify.R

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

fun getLoadOfGlide(context: Context, url: String?, imageView: ImageView) {
    Glide.with(context).load(url)/*.error(R.mipmap.icon_resident_front)*/.into(imageView)
}