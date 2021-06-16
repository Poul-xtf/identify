package com.wotransfer.identify.util.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.Nullable
import com.google.gson.Gson
import com.wotransfer.identify.R
import com.wotransfer.identify.util.getJson

class AnySearchList : RelativeLayout {

    private var mContext: Context? = context
    private var imageSrc: Int? = 0
    private var textsss: String? = ""
    private var ivTest: ImageView? = null
    private var tvEt: TextView? = null
    private var mListView: ExpandableListView? = null
    private var mySearchView: NySearchView? = null
    private var thisData: List<Model>? = null
    private var indexData = arrayListOf<String>()

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
        mySearchView = rootView.findViewById(R.id.my_search_view)
        mySearchView?.setItemListener {
            indexData.forEachIndexed { index, model ->
                if (it == model) {
                    mListView?.smoothScrollToPosition(index + 1)
                }
            }
        }
        tvEt?.text = textsss
    }

    var size = 0
    private fun getSize(index: Int): Int {
        var position = index
        if (index < 0) {
            return size
        }
        size += thisData!![index].data.size
        position -= 1
        getSize(position)
        return 0
    }

    override fun onNestedFling(
        target: View?,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean,
    ): Boolean {
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }


//    fun setListData(data: List<String>) {
//        thisData = data as ArrayList<String>
//        setViewAdapter()
//    }

    fun setViewAdapter() {
        val gson = Gson()
        val strData = getJson("Country.json", mContext!!)
        val countryBean = gson.fromJson(strData, CountryBean::class.java)
        thisData = countryBean.model
        val viewAdapter = AnyAdapter(mContext, thisData!!)
        mListView?.setGroupIndicator(null)
        mListView?.setAdapter(viewAdapter)
        val count = mListView?.count!!
        for (i in 0 until count) {
            mListView?.expandGroup(i)
        }
        mListView?.setOnGroupClickListener { _, _, _, _ ->
            true
        }
        thisData?.forEachIndexed { index, model ->
            indexData.add(model.index)
            model.data.forEach {
                indexData.add(it.CountryName)
            }
        }
        Log.d("xtf--->", indexData.toString())
    }
}