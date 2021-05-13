package com.wolfsea.defineviewgroupdemo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import java.lang.RuntimeException
import kotlin.math.max

/**
 *@desc
 *@author liuliheng
 *@time 2021/5/4  21:11
 **/
class FlowLayout  : ViewGroup{

    /*
    * 所有的子view
    * */
    private lateinit var allViews: MutableList<MutableList<View>>

    /*
    * 所有的行高
    * */
    private lateinit var linesHeight: MutableList<Int>

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (widthMode == MeasureSpec.AT_MOST) {
            throw RuntimeException("FlowLayout:layout_width must be set to wrap_content.")
        }

        var mHeight = paddingBottom + paddingTop

        //行高
        var lineHeight = 0
        //行宽
        var lineWidth  = 0

        allViews.clear()
        linesHeight.clear()

        var lineViews : MutableList<View> = mutableListOf()

        for (i in 0 until childCount) {

            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            val marginLayoutParams = child.layoutParams as MarginLayoutParams

            val childLineWidth =
                child.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
            val childLineHeight =
                child.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin

            //考虑padding
            if (childLineWidth + lineWidth > (widthSize - paddingLeft - paddingRight)) {

                //换行
                mHeight += lineHeight
                lineWidth += childLineWidth

                //添加子view到集合
                allViews.add(lineViews)
                linesHeight.add(lineHeight)

                lineViews = mutableListOf()
                lineViews.add(child)
            } else {

               //不换行
               lineHeight = max(lineHeight, childLineHeight)
               lineWidth += childLineWidth

               lineViews.add(child)
            }

            //最后一行
            if (i == childCount - 1) {

                mHeight += lineHeight
                linesHeight.add(lineHeight)
                allViews.add(lineViews)
            }
        }

        setMeasuredDimension(widthSize,if (heightMode == MeasureSpec.AT_MOST) mHeight else heightSize)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        var mLeft = paddingLeft
        var mTop = paddingTop

        for (i in 0 until allViews.size) {

            val lineViews = allViews[i]
            val lineHeight = linesHeight[i]

            for (j in 0 until lineViews.size) {

                val child = lineViews[i]
                if (child.visibility == GONE) {
                    continue
                }

                val layoutParams = child.layoutParams as MarginLayoutParams
                val lc = mLeft + layoutParams.leftMargin
                val tc = mTop + layoutParams.topMargin
                val rc = lc + child.measuredWidth
                val bc = tc + child.measuredHeight

                child.layout(lc, tc, rc, bc)

                mLeft += child.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            }

            mLeft = paddingLeft
            mTop += lineHeight
        }
    }

    /*
    * 重写布局参数
    * */
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    /**
     *@desc 初始化方法
     *@author:liuliheng
     *@time: 2021/5/4 21:12
    **/
    private fun init() {
        allViews = mutableListOf()
        linesHeight = mutableListOf()
    }
}