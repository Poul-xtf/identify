package com.wotransfer.identify.util.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.wotransfer.identify.R

class AnyAdapter : BaseExpandableListAdapter {

    private var mContext: Context? = null
    private var mListData: List<Model>? = null
    private var inflater: LayoutInflater? = null

    constructor(mContext: Context?, list: List<Model>) {
        this.mContext = mContext
        mListData = list
        inflater = LayoutInflater.from(mContext)
    }

    private var groupViewHolder: GroupViewHolder? = null
    private var itemListener: ItemListener? = null
    override fun getGroupCount(): Int = mListData!!.size
    override fun getGroup(p0: Int) = mListData?.get(p0)
    override fun getGroupId(p0: Int): Long = p0.toLong()

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        var myParentView = p2
        myParentView?.let {
            groupViewHolder = it.tag as GroupViewHolder
        } ?: let {
            myParentView = inflater
                ?.inflate(R.layout.item_view, p3, false)
            groupViewHolder = GroupViewHolder()
            groupViewHolder?.name = myParentView?.findViewById(R.id.name)
            groupViewHolder?.catalog = myParentView?.findViewById(R.id.catalog)
            myParentView?.tag = groupViewHolder
        }
        groupViewHolder?.catalog?.visibility = View.VISIBLE
        groupViewHolder?.name?.visibility = View.GONE
        groupViewHolder?.catalog?.text = mListData!![p0].index
        return myParentView!!
    }


    override fun getChildrenCount(p0: Int): Int = mListData!![p0].data.size

    override fun getChild(p0: Int, p1: Int) = mListData!![p0].data

    override fun getChildId(p0: Int, p1: Int): Long = p1.toLong()


    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        var childViewHolder: ChildViewHolder? = null
        var myView = p3
        myView?.let {
            childViewHolder = it.tag as ChildViewHolder
        } ?: let {
            myView = inflater
                ?.inflate(R.layout.item_view, p4, false)
            childViewHolder = ChildViewHolder()
            childViewHolder?.name = myView?.findViewById(R.id.name)
            childViewHolder?.catalog = myView?.findViewById(R.id.catalog)
            myView?.tag = childViewHolder
        }
        childViewHolder?.catalog?.visibility = View.GONE
        childViewHolder?.name?.visibility = View.VISIBLE
        childViewHolder?.name?.text = mListData!![p0].data[p1].CountryName
        childViewHolder?.name?.setOnClickListener {
            itemListener?.itemBack(p0, 0)
        }
        return myView!!
    }


    override fun hasStableIds(): Boolean = true


    override fun isChildSelectable(p0: Int, p1: Int): Boolean = true

    class GroupViewHolder {
        var name: TextView? = null
        var catalog: TextView? = null
    }

    class ChildViewHolder {
        var name: TextView? = null
        var catalog: TextView? = null
    }

    interface ItemListener {
        fun itemBack(position: Int, childPosition: Int)
    }

    fun setItemListener(itemListener: ItemListener) {
        this.itemListener = itemListener
    }

}