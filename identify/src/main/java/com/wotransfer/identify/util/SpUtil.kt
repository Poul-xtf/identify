package com.wotransfer.identify.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.wotransfer.identify.Constants

class SpUtil {

    companion object {
        fun putString(context: Context, key: String, text: String?) {
            val spUtil = context.getSharedPreferences(Constants.SP_KEY, MODE_PRIVATE)
            val edit: SharedPreferences.Editor = spUtil.edit()
            edit.putString(key, text).apply()
        }

        fun getString(context: Context?, key: String): String? {
            val spUtil = context?.getSharedPreferences(Constants.SP_KEY, MODE_PRIVATE)
            return spUtil?.getString(key, "null")
        }
    }

}