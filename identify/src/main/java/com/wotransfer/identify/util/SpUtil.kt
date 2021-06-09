package com.wotransfer.identify.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SpUtil {

    companion object {
//        var mContext: Context? = null
//        lateinit var sp_Util: SharedPreferences
//        fun register(context: Context) {
//            this.mContext = context
//            sp_Util = context.getSharedPreferences("local_mess", MODE_PRIVATE)
//        }

        fun putString(context: Context, key: String, text: String?) {
            val spUtil = context.getSharedPreferences("local_mess", MODE_PRIVATE)
            val edit: SharedPreferences.Editor = spUtil.edit()
            edit.putString(key, text).apply()
        }

        fun getString(context: Context?, key: String): String? {
            val spUtil = context?.getSharedPreferences("local_mess", MODE_PRIVATE)
            return spUtil?.getString(key, "null")
        }
    }

}