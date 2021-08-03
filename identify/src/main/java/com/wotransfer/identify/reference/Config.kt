package com.wotransfer.identify.reference

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.*

class Config<T> {

    /**
     * 供业务调用,配置sdk相关内容
     * 包括语言等
     */
    fun setConfig(mContext: Context, configType: ConfigType, value: T? = null) {
        dispatchConfig(mContext, configType, value)
    }

    private fun dispatchConfig(mContext: Context, type: ConfigType, value: T? = null) {
        when (type) {
            ConfigType.Locale -> {
                value?.let {
                    upDateResources(mContext, value as Locale)
                }
            }
        }
    }

    /**
     * 语言设置
     */
    private fun upDateResources(mContext: Context, locale: Locale) {
        Locale.setDefault(locale)
        val res: Resources = mContext.resources
        val config = Configuration(res.configuration)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                config.setLocale(locale)
                config.setLocales(localeList)
                context = mContext.createConfigurationContext(config)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                config.setLocale(locale)
                context = mContext.createConfigurationContext(config)
            }
            else -> {
                config.locale = locale
                res.updateConfiguration(config, res.displayMetrics)
            }
        }
    }

    enum class ConfigType {
        Locale
    }


    companion object{
        private lateinit var context:Context

        /**
         * 获取语言配置
         */
        fun getLocalConfig():Context{
            return context
        }
    }
}