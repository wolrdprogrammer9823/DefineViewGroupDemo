package com.wolfsea.defineviewgroupdemo

import android.content.Context
import android.view.ViewGroup

/**
 *@desc
 *@author liuliheng
 *@time 2021/5/11  22:39
 **/
class CustomLayout(context: Context) : ViewGroup(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //计算所有的子View宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        //保存宽高
        setMeasuredDimension(
            getDefaultSize(minimumWidth, widthMeasureSpec),
            getDefaultSize(minimumHeight, heightMeasureSpec)
        )
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

         //测量后子View的宽高
         var childMeasureWidth :Int
         var childMeasureHeight :Int

         //已经占据的宽和高
         var layoutWidth = 0
         var layoutHeight = 0

         //子View最大的高度
         var maxChildHeight = 0

        var newLeft :Int
        var newTop :Int
        var newRight :Int
        var newBottom :Int

        for (i in 0 until childCount) {

            val child = getChildAt(i)
            childMeasureWidth = child.measuredWidth
            childMeasureHeight = child.measuredHeight

            if (layoutWidth < width) {

                newLeft = layoutWidth
                newTop = top
                newRight = newLeft + childMeasureWidth
                newBottom = newTop + childMeasureHeight
            } else {

                //换行处理
                layoutWidth = 0
                layoutHeight += maxChildHeight

                newLeft = layoutWidth
                newTop = layoutHeight
                newRight = newLeft + childMeasureWidth
                newBottom = newTop + childMeasureHeight
            }

            layoutWidth += childMeasureWidth
            if (childMeasureHeight > maxChildHeight) {

                maxChildHeight = childMeasureHeight
            }

            child.layout(newLeft, newTop, newRight, newBottom)
        }
    }
}