package com.wolfsea.defineviewgroupdemo
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

/**
 *@desc  自定义ViewGroup
 *@author liuliheng
 *@time 2021/4/27  0:09
 **/
class DefineViewGroup : ViewGroup {

    private var dWidth = 0F
    private var dHeight = 0F

    /*
    * 子view高度
    * */
    var childHeight = 0F

    /*
     * 子view上内边距
    * */
    var childPaddingTop: Int = 0

    /*
    * 每一行默认的子view数量
    * */
    var itemCountPerRow: Int = 0

    /*
    * 子view文本大小
    * */
    var itemViewTextSize = 0f

    /*
    * 子view文本颜色
    * */
    var itemViewTextColor = 0

    /*
    * 子view背景色
    * */
    lateinit var itemViewBackground : Drawable

    /*
    * 子view边距
    * */
    var itemViewMargin = 0f

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

        init(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val mWidth = MeasureSpec.getSize(widthMeasureSpec)
        val mHeight = MeasureSpec.getSize(heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        when (widthMode) {
            MeasureSpec.EXACTLY -> {
                dWidth = (mWidth + marginLeft + marginRight).toFloat()
            }

            MeasureSpec.AT_MOST -> {
                dWidth = context.resources.getDimension(R.dimen.dp_200) + marginLeft + marginRight
                dWidth = mWidth.toFloat().coerceAtLeast(dWidth)
            }

            else -> {}
        }

        when (heightMode) {
            MeasureSpec.EXACTLY -> {
                dHeight = (mHeight + marginBottom + marginTop).toFloat()
            }

            MeasureSpec.AT_MOST -> {
                dHeight = context.resources.getDimension(R.dimen.dp_200) + marginBottom + marginTop
                dHeight = mHeight.toFloat().coerceAtLeast(dHeight)
            }

            else -> {}
        }

        setMeasuredDimension(dWidth.toInt(), dHeight.toInt())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val itemWidth = dWidth.minus(itemViewMargin).div(itemCountPerRow.toFloat()) - itemViewMargin

        for (i in 0 until childCount) {

            val child = getChildAt(i)

            val horizontalStep = i % itemCountPerRow
            val itemMargin = (if (horizontalStep == 0) 1 else horizontalStep + 1) * itemViewMargin
            val newLeft = left + horizontalStep * itemWidth + itemMargin

            val verticalStep = i / itemCountPerRow
            val spaceHeight = verticalStep * childHeight
            val newTop = (top + itemViewMargin * (verticalStep + 1) + spaceHeight).toInt()

            child.layout(
                    newLeft.toInt(),
                    newTop,
                    newLeft.toInt() + itemWidth.toInt(),
                    newTop + childHeight.toInt())

            child.setPadding(0, childPaddingTop , 0, 0)
        }
    }

    /**
     *@desc 初始化方法
     *@author:liuliheng
     *@time: 2021/4/27 0:11
    **/
    private fun init(context: Context) {
        childHeight = context.resources.getDimension(R.dimen.dp_45)
        childPaddingTop = childHeight.toInt() / 4
        itemCountPerRow = 5
        itemViewTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,18.0F,resources.displayMetrics)
        itemViewTextColor = context.resources.getColor(R.color.teal_200)
        itemViewBackground = context.resources.getDrawable(R.drawable.text_bg)
        itemViewMargin = context.resources.getDimension(R.dimen.dp_5)
    }

    fun setDataSourceByInflateView(dataSet: MutableList<String>?) {
        dataSet?.let {
            for (value in it) {
                val text = LayoutInflater.from(context).inflate(R.layout.item_view, null) as AppCompatTextView
                text.text = value
                addView(text)
            }
        }
    }

    fun setDataSourceByNewView(dataSet: MutableList<String>?) {
        dataSet?.let {
            for (value in it) {
                val text = AppCompatTextView(context)
                text.text = value
                text.textSize = itemViewTextSize
                text.setTextColor(itemViewTextColor)
                text.background = itemViewBackground
                text.gravity = Gravity.CENTER
                addView(text)
            }
        }
    }

}