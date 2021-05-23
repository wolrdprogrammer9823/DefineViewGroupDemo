package com.wolfsea.defineviewgroupdemo
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 *@desc  圆角矩形进度条
 *@author liuliheng
 *@time 2021/4/20  23:43
 **/
class RoundRectProgressBar(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private lateinit var backgroundPaint: Paint
    private lateinit var progressPaint: Paint

    private lateinit var backgroundRectF: RectF
    private lateinit var progressRectF: RectF
    private lateinit var middleProgressRectF: Rect

    private lateinit var progressGradientDrawable: LinearGradient

    private var progressWidth = 0
    private var progressHeight = 0

    private var currentProgress = 0F

    private var pieces = 0F

    private var rx = 0f
    private var ry = 0f

    init {
        init(context, attributeSet)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> {

                progressWidth = if (progressWidth == 0) MeasureSpec.getSize(widthMeasureSpec) else progressWidth
                progressWidth = progressWidth - paddingLeft - paddingRight
            }
            MeasureSpec.AT_MOST -> {

                val defaultWidth = resources.getDimension(R.dimen.dp_100).toInt()
                progressWidth =
                    defaultWidth.coerceAtMost(MeasureSpec.getSize(widthMeasureSpec)) - paddingLeft - paddingRight
            }
            MeasureSpec.UNSPECIFIED -> {}
            else -> {}
        }

        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> {

                progressHeight = MeasureSpec.getSize(heightMeasureSpec)
                progressHeight = progressHeight - paddingTop - paddingBottom
            }
            MeasureSpec.AT_MOST -> {

                val defaultHeight = if (progressHeight == 0) MeasureSpec.getSize(heightMeasureSpec) else progressHeight
                progressHeight =
                    defaultHeight.coerceAtMost(MeasureSpec.getSize(widthMeasureSpec)) - paddingTop - paddingBottom
            }
            MeasureSpec.UNSPECIFIED -> {}
            else -> {}
        }

        pieces = progressWidth / TOTAL_PIECES
    }

    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.apply {
            save()
            
            backgroundRectF.set(0F, 0F, progressWidth.toFloat(), progressHeight.toFloat())
            drawRoundRect(backgroundRectF, rx, ry, backgroundPaint)

            progressRectF.set(0F, 0F, currentProgress.times(pieces), progressHeight.toFloat())
            if (!currentProgress.equals(TOTAL_PIECES)) {

                clipRect(0F, 0F, currentProgress.times(pieces).minus(rx), progressHeight.toFloat())
            }
            drawRoundRect(progressRectF, rx, ry, progressPaint)

            restore()
        }
    }

    fun updateProgress(progress: Float) {
        currentProgress = progress
        postInvalidate()
    }

    //初始化方法
    private fun init(context: Context, attributeSet: AttributeSet) {

        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.color = resources.getColor(R.color.teal_200)
        backgroundPaint.style = Paint.Style.FILL

        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //progressPaint.color = resources.getColor(R.color.teal_700)
        progressPaint.style = Paint.Style.FILL

        backgroundRectF = RectF()
        progressRectF = RectF()
        middleProgressRectF = Rect()

        progressGradientDrawable = LinearGradient(
            0F,
            0F,
            progressWidth.toFloat(),
            progressHeight.toFloat(),
            intArrayOf(Color.BLUE, Color.GREEN, Color.RED),
            null,
            Shader.TileMode.CLAMP
        )

        context.obtainStyledAttributes(attributeSet, R.styleable.RoundRectProgressBar)
            .apply {

                progressWidth =
                    getDimension(R.styleable.RoundRectProgressBar_roundBarWidth, 0F).toInt()
                progressHeight =
                    getDimension(R.styleable.RoundRectProgressBar_roundBarHeight, 0F).toInt()

                rx = getDimension(R.styleable.RoundRectProgressBar_roundBarRx, 20F)
                ry = getDimension(R.styleable.RoundRectProgressBar_roundBarRx, 20F)

                recycle()
            }

        progressPaint.shader = progressGradientDrawable
    }

    companion object {
        const val TOTAL_PIECES = 100F
    }

}