package com.wotransfer.identify.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.wotransfer.identify.R

class ScanningView(context: Context?) : View(context) {

    private var mContext: Context? = null
    private var paint: Paint = Paint()
    private var viewWidth: Int? = 348
    private var viewHeight: Int? = 225
    private var localY = 0f
    private var screenWidth = 0
    private var screenHeight = 0

    constructor(context: Context?, attributeSet: AttributeSet) : this(context) {
        mContext = context
        paint.style = Paint.Style.FILL
        paint.textSize = 10f
        initTypeArray(attributeSet)
    }


    private fun initTypeArray(attrs: AttributeSet) {
        val obtain = mContext?.obtainStyledAttributes(attrs, R.styleable.scanning)
        viewWidth = obtain?.getInteger(R.styleable.scanning_sc_width, 0)
        viewHeight = obtain?.getInteger(R.styleable.scanning_sc_height, 0)
        obtain?.recycle()
        screenWidth = getScreenWidth()
        screenHeight = getScreenHeight()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(screenWidth, screenHeight)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = Color.GRAY
        paint.style = Paint.Style.STROKE
        val rectf = RectF(30f, 0f, screenWidth.toFloat() - 30f, (screenHeight / 4).toFloat())
        canvas?.drawRoundRect(rectf, 10f, 10f, paint)

        paint.color = Color.WHITE
        canvas?.drawLine(20f, localY, screenWidth.toFloat() - 20f, localY, paint)
        if (localY < (screenHeight / 4).toFloat())
            localY += 2
        else
            localY = 0f
        invalidate()
    }

    private fun getScreenWidth(): Int {
        val outMetrics = DisplayMetrics()
        (mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
            outMetrics
        )
        return outMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        val outMetrics = DisplayMetrics()
        (mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
            outMetrics
        )
        return outMetrics.heightPixels
    }

}