package com.wotransfer.identify_ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.wotransfer.identify_ui.R
import kotlinx.android.synthetic.main.dialog_tip_view.*

class TipDialogFragment/* constructor(*//*private val meeting: MeetingBean?,*//* val success: () -> Unit)*/ :
    DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.dialog_tip_view, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_sure.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.run {
//            val myAttributes = attributes.apply {
//                width = 900
//                height = 1120
//            }
//            attributes = myAttributes
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}
