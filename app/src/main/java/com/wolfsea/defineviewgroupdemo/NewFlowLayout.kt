package com.wolfsea.defineviewgroupdemo
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

/**
 *@desc  新的FlowLayout
 *@author liuliheng
 *@time 2021/5/5  21:32
 **/
class NewFlowLayout(context: Context, attributeSet: AttributeSet) : ViewGroup(context, attributeSet) {

    private var mContext: Context = context
    private var usefulWidth = 0
    private var lineSpace = 0

    var childList = mutableListOf<View>()
    var lineNumberList = mutableListOf<Int>()

    init {
        val typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.NewFlowLayout)
        lineSpace = typedArray.getDimensionPixelSize(R.styleable.NewFlowLayout_lineSpace,0)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var lineUsed = paddingLeft + paddingRight
        var lineY = paddingTop
        var lineHeight = 0

        for (i in 0 until childCount) {

            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }

            var spaceWidth = 0
            var spaceHeight = 0

            val childLayoutParams = child.layoutParams
            if (childLayoutParams is MarginLayoutParams) {

                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, lineY)
                spaceWidth += childLayoutParams.leftMargin + childLayoutParams.rightMargin
                spaceHeight += childLayoutParams.topMargin + childLayoutParams.bottomMargin
            } else {

                measureChild(child, widthMeasureSpec, heightMeasureSpec)
            }

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            spaceWidth += childWidth
            spaceHeight += childHeight

            if (lineUsed + spaceWidth > widthSize) {

                lineY += lineHeight + lineSpace
                lineUsed = paddingLeft + paddingRight
                lineHeight = 0
            }

            if (spaceHeight > lineHeight) {
                lineHeight = spaceHeight
            }

            lineUsed += spaceWidth
        }

        setMeasuredDimension(
            widthSize,
            if (heightMode == MeasureSpec.EXACTLY) heightSize else lineY + lineHeight + paddingTop
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        var lineX = paddingLeft
        var lineY = paddingTop

        val lineWidth = right - left
        usefulWidth = lineWidth - paddingLeft - paddingRight

        var lineUsed = paddingLeft + paddingRight
        var lineHeight = 0
        var lineNumber = 0

        lineNumberList.clear()

        for (i in 0 until childCount) {

            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }

            var spaceWidth = 0
            var spaceHeight = 0

            var childLeft: Int
            var childTop: Int
            var childRight: Int
            var childBottom: Int

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            val layoutParams = child.layoutParams
            if (layoutParams is MarginLayoutParams) {

               spaceWidth = layoutParams.leftMargin + layoutParams.rightMargin
               spaceHeight = layoutParams.topMargin + layoutParams.bottomMargin

               childLeft = lineX + layoutParams.leftMargin
               childTop = lineY + layoutParams.topMargin
               childRight = childLeft + childWidth
               childBottom = childTop + childHeight
            } else {

               childLeft = lineX
               childTop = lineY
               childRight = childLeft + childWidth
               childBottom = childTop + childHeight
            }

            spaceWidth += childWidth
            spaceHeight += childHeight

            if (lineUsed + spaceWidth > lineWidth) {

                lineNumberList.add(lineNumber)

                lineY += lineHeight + lineSpace
                lineUsed = paddingLeft + paddingRight
                lineX = paddingLeft
                lineHeight = 0
                lineNumber = 0

                if (layoutParams is MarginLayoutParams) {

                    childLeft = lineX + layoutParams.leftMargin
                    childTop = lineY + layoutParams.topMargin
                    childRight = childLeft + childWidth
                    childBottom = childTop + childHeight
                } else {

                    childLeft = lineX
                    childTop = lineY
                    childRight = childLeft + childWidth
                    childBottom = childTop + childHeight
                }

                child.layout(childLeft, childTop, childRight, childBottom)
                lineNumber++
                if (spaceHeight > lineHeight) {

                    lineHeight = spaceHeight
                }
                lineUsed += lineWidth
                lineX += lineWidth
            }
        }

        lineNumberList.add(lineNumber)
    }

    override fun generateLayoutParams(attrs: AttributeSet?):
            LayoutParams = MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?):
            LayoutParams = MarginLayoutParams(p)

    override fun generateDefaultLayoutParams():
            LayoutParams = MarginLayoutParams(super.generateDefaultLayoutParams())


    /*
     * 重新布局压缩多余的空间
     **/
    fun relayoutToCompress() {
        post {
            compress()
        }
    }

    /*
    * 压缩--去除不用的View
    * */
    private fun compress() {
        if (childCount == 0) {
            return
        }

        var count = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            count++
        }

        val children = arrayOfNulls<View>(count)
        val spaces = arrayOfNulls<Int>(count)

        var index = 0
        for (i in 0 until childCount) {

            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }

            children[index] = child
            val layoutParams = child.layoutParams
            if (layoutParams is MarginLayoutParams) {

                spaces[index] = child.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            } else {

                spaces[index] = child.measuredWidth
            }

            index++
        }

        val compressSpaces = arrayOfNulls<Int>(count)
        for (i in 0 until count) {

            compressSpaces[i] = if (spaces[i]!! > usefulWidth) usefulWidth else spaces[i]
        }

        sortToCompress(children = children, spaces = spaces)
        this.removeAllViews()
        for (child in childList) {

            this.addView(child)
        }
        childList.clear()
    }

    private fun sortToCompress(children: Array<View?>, spaces: Array<Int?>) {
         //二维数组
         val table = Array(children.size + 1) { IntArray(usefulWidth + 1) }
         for (i in 0 until children.size + 1) {
             for (j in 0 until usefulWidth) {
                 table[i][j] = 0
             }
         }

        val flag = BooleanArray(children.size)
        for (i in flag.indices) {
            flag[i] = false
        }

        for (i in 1 until childCount + 1) {

            for (j in spaces[i - 1]?.until(usefulWidth + 1)!!) {

                 table[i][j] = max(table[i -1][j],table[i -1][j - spaces[i -1]!!] + spaces[i -1]!!)
            }
        }

        var v = usefulWidth
        var i = children.size
        while (i > 0) {
            if (v > spaces[i - 1]!!) {
                if (table[i - 1][v] == table[i -1][v - spaces[i -1]!!] + spaces[i - 1]!!) {
                    flag[i -1] = true
                    v -= spaces[i - 1]!!
                }
                i--
            }
        }

        var rest = children.size
        for (j in flag.indices) {
            if (flag[j]) {
                childList.add(children[i]!!)
                rest--
            }
        }

        if (rest == 0) {
            return
        }

        val restArray = arrayOfNulls<View>(rest)
        val restSpaces = arrayOfNulls<Int>(rest)
        var index = 0
        for (j in flag.indices) {
            if (!flag[i]) {

                restArray[j] = children[j]
                restSpaces[j] = spaces[j]
                index++
            }
        }

        //递归调用
        sortToCompress(restArray, restSpaces)
    }

    /*
    * 重新布局对齐处理
    * */
    fun relayoutToAlign() {
        post {
            align()
        }
    }

    /*
     * 对齐处理
     * */
    private fun align() {
        if (childCount == 0) {
            return
        }

        var count = 0
        for (i in 0 until childCount) {

            val child = getChildAt(i)
            if (child is BlankView) {

                continue
            }
            count++
        }

        val newChildren = arrayOfNulls<View>(count)
        val newSpaces = arrayOfNulls<Int>(count)
        var n = 0
        for (i in 0 until childCount) {

            val child = getChildAt(i)
            if (child is BlankView) {
                continue
            }

            newChildren[n] = child
            val layoutParams = child.layoutParams
            if (layoutParams is MarginLayoutParams) {

                newSpaces[n] = layoutParams.leftMargin + layoutParams.rightMargin + child.measuredWidth
            } else {

                newSpaces[n] = child.measuredWidth
            }

            n++
        }

        var lineTotal = 0
        var start = 0
        this.removeAllViews()
        var newI = 0
        while (newI < count) {

            if (lineTotal + newSpaces[newI]!! > usefulWidth) {

                val blankWidth = usefulWidth - lineTotal
                val end = newI - 1
                val blankCount = end - start
                if (blankCount >= 0) {
                    if (blankCount > 0) {

                        val eachBlankWidth = blankWidth / blankCount
                        val layoutParams = MarginLayoutParams(eachBlankWidth, 0)
                        for (j in start until end) {

                            this.addView(newChildren[newI])
                            val blankView = BlankView(context)
                            this.addView(blankView, layoutParams)
                        }
                    }

                    this.addView(newChildren[end])
                    start = newI
                    newI--
                } else {

                   this.addView(newChildren[newI])
                   start = newI + 1
                }

                lineTotal = 0
            } else {
                lineTotal += newSpaces[newI]!!
            }

            newI++
        }

        for (j in start until count) {

            this.addView(newChildren[j])
        }
    }

    /*
    * 具体显示某几行
    * */
    fun specifyLines(line_number_now : Int) {
        post {

            val lineNumber = if (line_number_now > lineNumberList.size) lineNumberList.size else line_number_now
            var childNumber = 0
            for (i in 0 until lineNumber) {

                childNumber += lineNumberList[i]
            }

            val childrenList = mutableListOf<View>()
            for (i in 0 until childNumber) {

                childrenList.add(childList[i])
            }

            removeAllViews()

            for (child in childrenList) {
                addView(child)
            }
        }
    }

    /*
    * 占位使用的view
    * */
    inner class BlankView(context: Context) : View(context)
}