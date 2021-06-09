package com.wotransfer.identify_ui.util

import android.content.Context
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.wotransfer.identify_ui.R

fun <T> showOptionsPickerView(
    context: Context,
    titleRes: Int,
    onOptionsSelectListener: OnOptionsSelectListener?,
    dataList: List<T>?,
): OptionsPickerView<*>? {
    return showOptionsPickerView(context,
        context.getString(titleRes),
        onOptionsSelectListener,
        dataList)
}

fun <T> showOptionsPickerView(
    context: Context,
    title: String?,
    onOptionsSelectListener: OnOptionsSelectListener?,
    dataList: List<T>?,
): OptionsPickerView<Any>? {
    val pvOptions = OptionsPickerBuilder(context, onOptionsSelectListener)
        .setSubmitText(context.getString(R.string.i_sure)) //确定按钮文字
        .setCancelText(context.getString(R.string.i_cancel)) //取消按钮文字
        .setTitleText(title) //标题
        .setSubCalSize(18) //确定和取消文字大小
        .setTitleSize(16) //标题文字大小
//        .setTitleColor(ContextCompat.getColor(context, R.color.text_black)) //标题文字颜色
//        .setSubmitColor(ContextCompat.getColor(context, R.color.main_style)) //确定按钮文字颜色
//        .setCancelColor(ContextCompat.getColor(context, R.color.text_grey)) //取消按钮文字颜色
//        .setTitleBgColor(ContextCompat.getColor(context, R.color.white)) //标题背景颜色 Night mode
//        .setBgColor(ContextCompat.getColor(context, R.color.white)) //滚轮背景颜色 Night mode
        .setContentTextSize(18) //滚轮文字大小
        .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
        .setSelectOptions(0) //设置默认选中项
        .setOutSideCancelable(true) //点击外部dismiss default true
        .isDialog(false) //是否显示为对话框样式
        .isRestoreItem(false) //切换时是否还原，设置默认选中第一项。
        .build<Any>()
    pvOptions.setPicker(dataList) //添加数据源
    pvOptions.show()
    return pvOptions
}
