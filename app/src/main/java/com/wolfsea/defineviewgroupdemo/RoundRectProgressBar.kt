package com.wolfsea.defineviewgroupdemo
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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

    init {
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> {

                progressWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
            }
            MeasureSpec.AT_MOST -> {

                val defaultWidth = resources.getDimension(R.dimen.dp_100).toInt()
                progressWidth = defaultWidth.coerceAtMost(MeasureSpec.getSize(widthMeasureSpec)) - paddingLeft - paddingRight
            }
            MeasureSpec.UNSPECIFIED -> {}
            else -> {}
        }

        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> {

                progressHeight = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
            }
            MeasureSpec.AT_MOST -> {

                val defaultHeight = resources.getDimension(R.dimen.dp_25).toInt()
                progressHeight = defaultHeight.coerceAtMost(MeasureSpec.getSize(widthMeasureSpec)) - paddingLeft - paddingRight
            }
            MeasureSpec.UNSPECIFIED -> {}
            else -> {}
        }

        pieces = progressWidth / TOTAL_PIECES

        progressGradientDrawable = LinearGradient(
            0F,
            0F,
            progressWidth.toFloat(),
            progressHeight.toFloat(),
            intArrayOf(Color.BLUE, Color.GREEN, Color.RED),
            null,
            Shader.TileMode.CLAMP
        )

        progressPaint.shader = progressGradientDrawable
    }

    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.apply {
            save()
            
            backgroundRectF.set(0F, 0F, progressWidth.toFloat(), progressHeight.toFloat())
            drawRoundRect(backgroundRectF, RX, RY, backgroundPaint)

            progressRectF.set(0F, 0F, currentProgress.times(pieces), progressHeight.toFloat())
            if (!currentProgress.equals(TOTAL_PIECES)) {

                clipRect(0F, 0F, currentProgress.times(pieces).minus(RX), progressHeight.toFloat())
            }
            drawRoundRect(progressRectF, RX, RY, progressPaint)

            restore()
        }
    }

    fun updateProgress(progress: Float) {
        currentProgress = progress
        postInvalidate()
    }

    //初始化方法
    private fun init() {

        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.color = resources.getColor(R.color.teal_200)
        backgroundPaint.style = Paint.Style.FILL

        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //progressPaint.color = resources.getColor(R.color.teal_700)
        progressPaint.style = Paint.Style.FILL

        backgroundRectF = RectF()
        progressRectF = RectF()
        middleProgressRectF = Rect()
    }

    companion object {
        const val RX = 15F
        const val RY = 15F
        const val TOTAL_PIECES = 100F
    }

}