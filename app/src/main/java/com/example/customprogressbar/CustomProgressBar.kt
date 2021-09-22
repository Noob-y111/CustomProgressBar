package com.example.customprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.Range
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import java.lang.Exception
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class CustomProgressBar : View {

    companion object {
        private const val TAG = "CustomProgressBar"
    }

    //attrs
    @ColorInt
    private var firstColor = Color.BLUE

    @ColorInt
    private var secondColor = Color.GREEN

    @ColorInt
    private var baseColor = Color.GRAY

    @ColorInt
    private var textColor = Color.BLACK
    private var progressDuration = 60
    private var secondPosition = 0
    private var firstPosition = 0
    private var lineWidth = dpToPx(5f)
    private var textSize = spToPx(12f)

    //run time
    private var defaultSize = dpToPx(50f)
    private var radius = 0
    private var baseLineDy = 0f

    //paint
    private val basePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val firstPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val secondPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val percentPaint = TextPaint().apply {
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        getAttrs(context, attrs)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }

    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            resources.displayMetrics
        )
    }

    private fun getAttrs(context: Context?, attrs: AttributeSet?) {
        context?.let { contextNotNull ->
            contextNotNull.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar).also {
                firstColor = it.getColor(R.styleable.CustomProgressBar_firstColor, firstColor)
                secondColor = it.getColor(R.styleable.CustomProgressBar_secondColor, secondColor)
                baseColor = it.getColor(R.styleable.CustomProgressBar_baseColor, baseColor)

                progressDuration =
                    it.getInt(R.styleable.CustomProgressBar_progressDuration, progressDuration)
                firstPosition =
                    it.getInt(R.styleable.CustomProgressBar_firstPosition, firstPosition)
                secondPosition =
                    it.getInt(R.styleable.CustomProgressBar_secondPosition, secondPosition)
                lineWidth = it.getDimension(R.styleable.CustomProgressBar_lineWidth, lineWidth)
                textSize = it.getDimension(R.styleable.CustomProgressBar_centerTextSize, textSize)
                textColor = it.getColor(R.styleable.CustomProgressBar_centerTextColor, textColor)
                it.recycle()
            }
        }
        initPaint()
    }

    private fun initPaint() {
        basePaint.apply {
            color = baseColor
            strokeWidth = lineWidth
        }

        firstPaint.apply {
            color = firstColor
            strokeWidth = lineWidth
        }

        secondPaint.apply {
            color = secondColor
            strokeWidth = lineWidth
        }

        percentPaint.apply {
            color = textColor
            textSize = this@CustomProgressBar.textSize

        }
        val fontMetrics = percentPaint.fontMetrics
        baseLineDy = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        Log.d(TAG, "initPaint: baseLineDy ==> $baseLineDy")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val targetSize = max(min(widthSize, heightSize), defaultSize.toInt())
        val targetMeasureSpec = MeasureSpec.makeMeasureSpec(targetSize, MeasureSpec.EXACTLY)
        radius = targetSize / 2
        setMeasuredDimension(targetMeasureSpec, targetMeasureSpec)
    }

    private fun getRadius(): Float {
        return radius - lineWidth
    }

    private fun getPercent(position: Int): Float {
        return position.toFloat() / progressDuration
    }

    private fun getAngle(position: Int): Float {
        return getPercent(position) * 360
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawCircle(
            (measuredWidth / 2).toFloat(),
            (measuredHeight / 2).toFloat(),
            getRadius(),
            basePaint
        )

        if (secondPosition != 0) {
            canvas?.drawArc(
                lineWidth,
                lineWidth,
                getRadius() * 2 + lineWidth,
                getRadius() * 2 + lineWidth,
                90f,
                getAngle(secondPosition),
                false,
                secondPaint
            )
        }
        if (firstPosition != 0) {
            canvas?.drawArc(
                lineWidth,
                lineWidth,
                getRadius() * 2 + lineWidth,
                getRadius() * 2 + lineWidth,
                90f,
                getAngle(firstPosition),
                false,
                firstPaint
            )
        }

        canvas?.drawText(
            "${(getPercent(firstPosition) * 100).roundToInt()}%",
            radius.toFloat(),
            radius.toFloat() + baseLineDy,
            percentPaint
        )
    }

    fun toFirstPosition(position: Int) {
        if (Range(0, progressDuration).contains(position)) {
            firstPosition = position
            postInvalidate()
        } else {
            throw Exception("out of range")
        }
    }

    fun toSecondPosition(position: Int) {
        if (Range(0, progressDuration).contains(position)) {
            secondPosition = position
            postInvalidate()
        } else {
            throw Exception("out of range")
        }
    }
}