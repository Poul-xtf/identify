package com.wotransfer.identify_ui.util.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.wotransfer.identify_ui.R

class AnyAdapter : BaseAdapter {
    private var mContext: Context? = null
    private var mList: List<String>? = null
    private var inflater: LayoutInflater? = null

    constructor(mContext: Context?, list: List<String>) {
        this.mContext = mContext
        mList = list
        inflater = LayoutInflater.from(mContext)
    }

    override fun getCount(): Int = mList?.size ?: 0

    override fun getItem(p0: Int): Any = mList!![p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var holder: MyViewHolder? = null
        var myView = p1
        myView?.let {
            holder = it.tag as MyViewHolder
        } ?: let {
            holder = MyViewHolder()
            myView = inflater!!.inflate(R.layout.item_view, null)
            holder!!.catalog = myView!!.findViewById(R.id.catalog)
            holder!!.name = myView!!.findViewById(R.id.name)
            myView!!.tag = holder
        }
        holder!!.name!!.text = mList!![p0]
        return myView!!
    }

    class MyViewHolder {
        var catalog: TextView? = null
        var name: TextView? = null
    }
}