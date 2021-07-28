package com.wotransfer.identify.ui

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

abstract class BaseKycActivity<T : ViewBinding> : Activity() {

    var resultBackCode = 1
    var requestCode = 0
    lateinit var binding: T

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutView()
        initView()
        val flagTranslucentNavigation: Int =
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            val attributes: WindowManager.LayoutParams = window.attributes
            attributes.flags = attributes.flags or flagTranslucentNavigation
            window.attributes = attributes
            getWindow().statusBarColor = Color.TRANSPARENT
        }
        setAndroidNativeLightStatusBar()
    }

    private fun layoutView() {
        val type: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
        val cls = type.actualTypeArguments[0] as Class<*>
        try {
            val inflate: Method = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
            binding = inflate.invoke(null, layoutInflater) as T
            setContentView(binding.root)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setAndroidNativeLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                APPEARANCE_LIGHT_NAVIGATION_BARS,
                APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    abstract fun initView()
}