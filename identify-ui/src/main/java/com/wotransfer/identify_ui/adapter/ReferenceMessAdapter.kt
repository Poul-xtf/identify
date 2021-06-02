package com.wotransfer.identify_ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.wotransfer.identify_ui.R
import com.wotransfer.identify_ui.reference.bean.IdConfigForSdkRO
import com.wotransfer.identify_ui.util.getDrawable

class ReferenceMessAdapter(
    context: Context,
    mList: List<IdConfigForSdkRO>,
) : BaseExpandableListAdapter() {

    private var mContext = context
    private var mListData = mList
    private var inflater = LayoutInflater.from(context)
    private var groupViewHolder: GroupViewHolder? = null
    private var itemListener: ItemListener? = null
    override fun getGroupCount(): Int = mListData.size
    override fun getGroup(p0: Int) = mListData[p0]
    override fun getGroupId(p0: Int): Long = p0.toLong()

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        var myParentView = p2
        myParentView?.let {
            groupViewHolder = it.tag as GroupViewHolder
        } ?: let {
            myParentView = inflater
                .inflate(R.layout.expand_parent_item, p3, false)
            groupViewHolder = GroupViewHolder()
            groupViewHolder?.tvTitle = myParentView?.findViewById(R.id.tv_title)
            groupViewHolder?.ivStatus = myParentView?.findViewById(R.id.iv_status)
            groupViewHolder?.parentView = myParentView?.findViewById(R.id.parent_view)
            myParentView?.tag = groupViewHolder
        }
        groupViewHolder?.tvTitle?.text = mListData[p0].idName
        groupViewHolder?.parentView?.getDrawable(mContext, R.drawable.shape_item)
        groupViewHolder?.ivStatus?.getDrawable(mContext, R.mipmap.icon_item_open)
        return myParentView!!
    }


    override fun getChildrenCount(p0: Int): Int = 1

    override fun getChild(p0: Int, p1: Int) = mListData[p0].idConfigForSdkROList

    override fun getChildId(p0: Int, p1: Int): Long = p1.toLong()


    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        groupViewHolder?.parentView?.getDrawable(mContext, R.drawable.shape_item_top)
        groupViewHolder?.ivStatus?.getDrawable(mContext, R.mipmap.icon_item_close)

        var childViewHolder: ChildViewHolder? = null
        var myView = p3
        myView?.let {
            childViewHolder = it.tag as ChildViewHolder
        } ?: let {
            myView = inflater
                .inflate(R.layout.expand_child_item, p4, false)
            childViewHolder = ChildViewHolder()
            childViewHolder?.ivCarFront = myView?.findViewById(R.id.iv_car_front)
            childViewHolder?.ivCarBack = myView?.findViewById(R.id.iv_car_back)
            childViewHolder?.tvCarFront = myView?.findViewById(R.id.tv_car_front)
            childViewHolder?.tvCarBack = myView?.findViewById(R.id.tv_car_back)
            childViewHolder?.rlLabel1 = myView?.findViewById(R.id.rl_label_1)
            childViewHolder?.rlLabel2 = myView?.findViewById(R.id.rl_label_2)
            myView?.tag = childViewHolder
        }
        childViewHolder?.rlLabel2?.visibility = View.VISIBLE
//        when (mListData[p0].type) {
//            ReferenceEnum.PASSPORT.value() -> {
//                childViewHolder?.ivCarFront?.getDrawable(mContext, R.mipmap.icon_passport)
//                childViewHolder?.ivCarBack?.getDrawable(mContext, R.mipmap.icon_passport)
//                childViewHolder?.tvCarBack?.text =
//                    mContext.getString(R.string.i_button_passport_back)
//                childViewHolder?.tvCarFront?.text =
//                    mContext.getString(R.string.i_button_passport_front)
//            }
//            ReferenceEnum.DRIVER.value() -> {
//                childViewHolder?.ivCarFront?.getDrawable(mContext, R.mipmap.icon_code)
//                childViewHolder?.ivCarBack?.getDrawable(mContext, R.mipmap.icon_camera_take)
//
//                childViewHolder?.tvCarBack?.text =
//                    mContext.getString(R.string.i_button_car_back)
//                childViewHolder?.tvCarFront?.text =
//                    mContext.getString(R.string.i_button_car_front)
//            }
//            ReferenceEnum.IDENTIFY.value() -> {
//                childViewHolder?.ivCarFront?.getDrawable(mContext, R.mipmap.icon_code)
//                childViewHolder?.ivCarBack?.getDrawable(mContext, R.mipmap.icon_code)
//                childViewHolder?.tvCarBack?.text =
//                    mContext.getString(R.string.i_button_identify_back)
//                childViewHolder?.tvCarFront?.text =
//                    mContext.getString(R.string.i_button_identify_front)
//            }
//            ReferenceEnum.VISA.value() -> {
//                childViewHolder?.ivCarFront?.getDrawable(mContext, R.mipmap.icon_passport)
//
//                childViewHolder?.rlLabel2?.visibility = View.INVISIBLE
//                childViewHolder?.tvCarFront?.text =
//                    mContext.getString(R.string.i_button_visa)
//            }
//        }
        childViewHolder?.rlLabel1?.setOnClickListener {
            itemListener?.itemBack()
        }
        return myView!!
    }


    override fun hasStableIds(): Boolean = true


    override fun isChildSelectable(p0: Int, p1: Int): Boolean = true

    class GroupViewHolder {
        var tvTitle: TextView? = null
        var ivStatus: ImageView? = null
        var parentView: ConstraintLayout? = null
    }

    class ChildViewHolder {
        var ivCarFront: ImageView? = null
        var ivCarBack: ImageView? = null
        var tvCarBack: TextView? = null
        var tvCarFront: TextView? = null
        var rlLabel1: RelativeLayout? = null
        var rlLabel2: RelativeLayout? = null
    }

    interface ItemListener {
        fun itemBack()
    }

    fun setItemListener(itemListener: ItemListener) {
        this.itemListener = itemListener
    }

}