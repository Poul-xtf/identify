package com.wotransfer.identify.util.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.Nullable
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import com.wotransfer.identify.R
import com.wotransfer.identify.util.getJson
import kotlinx.android.synthetic.main.my_list_view.view.*

class AnySearchList : RelativeLayout {

    private var mContext: Context? = context
    private var imageSrc: Int? = 0
    private var textName: String? = ""
    private var tvEt: TextView? = null
    private var viewAdapter: AnyAdapter? = null
    private var mListView: ExpandableListView? = null
    private var mySearchView: NySearchView? = null
    private var thisData: List<Model>? = null
    private var tempData: List<Model>? = null
    private var indexData = arrayListOf<String>()

    constructor(context: Context) : this(context, null) {}

    constructor(context: Context, @Nullable attributeSet: AttributeSet?) : this(
        context,
        attributeSet,
        0
    ) {
    }

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, @Nullable attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        mContext = context
        val obtain = mContext?.obtainStyledAttributes(attributeSet, R.styleable.AnySearchList_view)
        imageSrc = obtain?.getResourceId(R.styleable.AnySearchList_view_image_src, 0)
        textName = obtain?.getString(R.styleable.AnySearchList_view_text_)
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
        tvEt?.text = textName
        et_search.addTextChangedListener {
            if (it.toString() == "") {
                thisData = tempData
                setData()
                return@addTextChangedListener
            }
            val myModel = arrayListOf<Model>()
            val listData = arrayListOf<Data>()
            tempData?.forEachIndexed { _, model ->
                model.data.forEachIndexed { _, data ->
                    if (it.toString() == data.countryName) {
//                        myModel.add(model)
                        listData.add(data)
                        myModel.add(Model(listData, model.index))
                        thisData = myModel
                        setData()
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    override fun onNestedFling(
        target: View?,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean,
    ): Boolean {
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }

    fun setViewAdapter(backListener: (String, String) -> Unit?) {
        val gson = Gson()
        val strData = getJson("Country.json", mContext!!)
        val countryBean = gson.fromJson(strData, CountryBean::class.java)
        tempData = countryBean.model
        thisData = countryBean.model
        setData()

        mListView?.setOnGroupClickListener { _, _, _, _ ->
            true
        }

        mListView?.setOnChildClickListener { _, _, pIndex, cIndex, _ ->
            val country = thisData?.get(pIndex)?.data?.get(cIndex)?.countryCode
            val countryName = thisData?.get(pIndex)?.data?.get(cIndex)?.countryName
            backListener.invoke(country!!, countryName!!)
            false
        }
        thisData?.forEachIndexed { _, model ->
            indexData.add(model.index)
            model.data.forEach {
                indexData.add(it.countryName)
            }
        }
    }

    private fun setData() {
        viewAdapter = AnyAdapter(mContext, thisData!!)
        mListView?.setGroupIndicator(null)
        mListView?.setAdapter(viewAdapter)
        val count = mListView?.count!!
        for (i in 0 until count) {
            mListView?.expandGroup(i)
        }
    }
}