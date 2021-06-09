package com.wotransfer.identify_ui.util.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.wotransfer.identify_ui.R

class AnySearchList : RelativeLayout {

    private var mContext: Context? = context
    private var imageSrc: Int? = 0
    private var textsss: String? = ""
    private var ivTest: ImageView? = null
    private var tvEt: TextView? = null
    private var mListView: ListView? = null
    private var thisData = arrayListOf<String>()

    constructor(context: Context) : this(context, null) {}

    constructor(context: Context, @Nullable attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        0
    ) {
    }

    constructor(context: Context, @Nullable attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        mContext = context
        val obtain = mContext?.obtainStyledAttributes(attributeSet, R.styleable.AnySearchList_view)
        imageSrc = obtain?.getResourceId(R.styleable.AnySearchList_view_image_src, 0)
        textsss = obtain?.getString(R.styleable.AnySearchList_view_text_)
        obtain?.recycle()
        finView()
    }

    private fun finView() {
        val rootView = LayoutInflater.from(mContext).inflate(R.layout.my_list_view, this)
        mListView = rootView.findViewById(R.id.mListView)
        tvEt?.text = textsss
    }

    override fun onNestedFling(
        target: View?,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }


    fun setListData(data: List<String>) {
        thisData = data as ArrayList<String>
        setViewAdapter()
    }

    private fun setViewAdapter() {
        val viewAdapter = AnyAdapter(mContext, thisData)
        mListView?.adapter = viewAdapter
    }
}