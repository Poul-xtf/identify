package com.wotransfer.identify.ui

import android.view.View
import com.wotransfer.identify.databinding.ActivityChangeViewBinding
import com.wotransfer.identify.databinding.ActivityIdentityViewBinding
import com.wotransfer.identify.databinding.ActivityKycViewBinding
import com.wotransfer.identify.databinding.ActivityOcrViewBinding

class ViewBindingDispatch<T> {
    fun getView(contentView: T): View {
        return when (contentView) {
            is ActivityOcrViewBinding -> {
                (contentView as ActivityOcrViewBinding).root
            }
            is ActivityChangeViewBinding -> {
                (contentView as ActivityChangeViewBinding).root
            }
            is ActivityIdentityViewBinding-> {
                (contentView as ActivityIdentityViewBinding).root
            }
            else -> {
                (contentView as ActivityKycViewBinding).root
            }
        }
    }
}