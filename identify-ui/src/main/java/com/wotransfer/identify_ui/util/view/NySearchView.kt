package com.wotransfer.identify_ui.util.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.wotransfer.identify_ui.R
import kotlin.math.abs

class NySearchView(context: Context) : View(context) {
    private var az = arrayListOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )
    private var azY = HashMap<String, Float>()

    private var mContext: Context? = null
    private var viewWidth: Int? = 0
    private var viewHeight: Int? = 0
    private var locationX: Int? = 0
    private var locationY: Int? = 0
    private var textColor: Int? = Color.RED
    private var textSize: Int? = 10
    private var paint: Paint = Paint()
    private var paint1: Paint = Paint()
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    init {
        paint.color = Color.WHITE
        paint.typeface = Typeface.DEFAULT_BOLD

    }

    constructor(context: Context, attr: AttributeSet) : this(context) {
        this.mContext = context
        screenWidth = getScreenWidth()
        screenHeight = getScreenWidth()
        setAttributes(attr)
    }


    private fun setAttributes(attrs: AttributeSet) {
        val obtain = mContext?.obtainStyledAttributes(attrs, R.styleable.AnySearchList)
        viewWidth = obtain?.getInteger(R.styleable.AnySearchList_view_width, 100)
        viewHeight =
            obtain?.getInteger(R.styleable.AnySearchList_view_height, getScreenHeight() - 500)
        locationX = obtain?.getInt(R.styleable.AnySearchList_locationX, 0)
        locationY = obtain?.getInt(R.styleable.AnySearchList_locationY, 0)
        textColor = obtain?.getInt(R.styleable.AnySearchList_textColor, Color.RED)
        textSize = obtain?.getInt(R.styleable.AnySearchList_textSize, 0)
        obtain?.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(viewWidth!!, viewHeight!!)
    }

    private var rawX = 0f
    private var rawY = 0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                rawX = event.rawX
                rawY = event.rawY
                Log.d("xtf->", "$rawY")
                Log.d("xtf->", "$azY")

                val fingerViewY = rawY
                if (fingerViewY > ((getScreenHeight() - viewHeight!!) / 2) && fingerViewY < getScreenHeight() - ((getScreenHeight() - viewHeight!!) / 2) && rawX > (getScreenWidth() - viewWidth!!)) {
                    val temp = arrayListOf<Float>()
                    val temp1 = arrayListOf<Float>()
                    azY.forEach { (key, value) ->
                        temp.add(abs(value - fingerViewY))
                        temp1.add(value)
                    }
                    Log.d("xtf->", "$temp")

                    temp.sort()
                    Log.d("xtf->", "$temp")
                    azY.forEach { (key, value) ->
                        if (abs(value - fingerViewY) == temp[0]) {
                            Toast.makeText(mContext, key, Toast.LENGTH_SHORT).show()
                            return@forEach
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {

            }
        }
        return false
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d("xxxx", "$screenWidth")
//        canvas?.drawRect(
//            0f,
//            0f,
//            viewWidth!!.toFloat(),
//            viewHeight!!.toFloat(),
//            paint
//        )
        //可能内存泄漏
        paint1.color = Color.BLACK
        paint1.textSize = 40f
        paint1.textAlign = Paint.Align.CENTER
        az.forEachIndexed { index, s ->
            canvas?.drawText(
                s,
                (viewWidth!! / 2).toFloat(),
                (index + 1) * ((viewHeight!! / 26).toFloat()),
                paint1
            )
            azY[s] = (index + 1) * (viewHeight!! / 26).toFloat() + 10f
        }
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

    private fun getStatusHeight(): Int {
        val resourceId: Int =
            mContext?.resources?.getIdentifier("status_bar_height", "dimen", "android")!!
        return mContext?.resources?.getDimensionPixelSize(resourceId)!!
    }

}