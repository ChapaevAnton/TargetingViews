package com.dpt.inclineviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.*


class InclinePlatformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val pxViewSize = 200
    private val pxStrokeWidth = 3F
    private val pxSerifWidth = 3F
    private val pxCenterSerifWidth = 6F
    private var pxSerifSize = 10F
    private val pxTextSize = 25F
    private var pxBitmapSize = 50

    private val backgroundPaint = createPaint("#90EE90").apply {
        style = Paint.Style.FILL
    }
    private val inclineBackgroundPaint = createPaint("#000000").apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = createPaint().apply {
        style = Paint.Style.STROKE
        strokeWidth = pxStrokeWidth
    }
    private val serifPaint = createPaint(colorString = "#FFFFFF").apply {
        style = Paint.Style.STROKE
        strokeWidth = pxSerifWidth
    }
    private val centerSerifPaint = createPaint(colorString = "#FFFFFF").apply {
        style = Paint.Style.STROKE
        strokeWidth = pxCenterSerifWidth
    }
    private val textPaint = createPaint(colorString = "#FFFFFF").apply {
        textSize = pxTextSize
        textAlign = Paint.Align.CENTER
    }
    private val bitmapPaint = createPaint()

    private var pxCanvasWidth = 0
    private var pxCanvasHeight = 0
    private var pxCanvasPadding = 0
    private var pxCanvasCenter = 0F
    private var pxCanvasRadius = 0F

    private val centerPoint = PointF()
    private val inclineRect = RectF()
    private val lineLeftRect = RectF()
    private val lineRightRect = RectF()
    private val serifRect = RectF()
    private val textPoint = PointF()
    private var bitmapMatrix = Matrix()

    @DrawableRes
    private var iconRes: Int = R.drawable.base_icon
    private var gradientMode = GradientMode.FRONT
    private var scaleMode = ScaleMode.NONE
    private var iconBitmap: Bitmap? = null
    private var isInverted = false
    private var isNightMode = false
    private var degreeText = "0,0º"
    private var degree = 0F

    init {
        if (attrs != null) {
            val attrArray = context.obtainStyledAttributes(attrs, R.styleable.InclinePlatformView)
            try {
                val gradientMode = attrArray.getInt(
                    R.styleable.InclinePlatformView_gradient_mode,
                    GradientMode.FRONT.ordinal
                ).let { GradientMode.values()[it] }

                val scaleMode = attrArray.getInt(
                    R.styleable.InclinePlatformView_scale_mode,
                    ScaleMode.NONE.ordinal
                ).let { ScaleMode.values()[it] }

                val inverted = attrArray.getBoolean(R.styleable.InclinePlatformView_inverted, false)

                val iconRes = attrArray.getResourceId(
                    R.styleable.InclinePlatformView_icon,
                    R.drawable.base_icon
                )

                val degree = attrArray.getFloat(R.styleable.InclinePlatformView_angle, 0F).toDouble()

                setGradientMode(gradientMode)
                setScaleMode(scaleMode)
                isInverted(inverted)
                setIcon(iconRes)
                setAngle(degree)
            } finally {
                attrArray.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthExpect = paddingLeft + paddingRight + pxViewSize
        val heightExpect = paddingTop + paddingBottom + pxViewSize
        val widthResolve = resolveSize(widthExpect, widthMeasureSpec)
        val heightResolve = resolveSize(heightExpect, heightMeasureSpec)
        val side = min(widthResolve, heightResolve)
        val paddingLeftRight = min(paddingLeft, paddingRight)
        val paddingTopBottom = min(paddingTop, paddingBottom)

        pxCanvasWidth = side
        pxCanvasHeight = side
        pxCanvasPadding = min(paddingLeftRight, paddingTopBottom)
        pxCanvasCenter = (side - pxCanvasPadding * 2) * 0.5F

        strokePaint.strokeWidth = (pxCanvasHeight * STROKE_WIDTH_SCALE).toFloat()
        serifPaint.strokeWidth = (pxCanvasHeight * SERIF_WIDTH_SCALE).toFloat()
        centerSerifPaint.strokeWidth = (pxCanvasHeight * CENTER_SERIF_WIDTH_SCALE).toFloat()
        pxSerifSize = (pxCanvasHeight * SERIF_SCALE).toFloat()
        textPaint.textSize = (pxCanvasHeight * TEXT_SCALE).toFloat()
        pxBitmapSize = (pxCanvasHeight * BITMAP_SCALE).toInt()

        pxCanvasRadius = pxCanvasCenter - strokePaint.strokeWidth

        centerPoint.set(pxCanvasCenter + pxCanvasPadding, pxCanvasCenter + pxCanvasPadding)
        inclineRect.set(
            pxCanvasPadding + strokePaint.strokeWidth,
            pxCanvasPadding + strokePaint.strokeWidth,
            pxCanvasWidth - inclineRect.left,
            pxCanvasHeight - inclineRect.top
        )
        lineLeftRect.set(
            inclineRect.left,
            centerPoint.y,
            pxSerifSize * 3 + pxCanvasPadding,
            centerPoint.y,
        )
        lineRightRect.set(
            pxCanvasWidth - lineLeftRect.right,
            centerPoint.y,
            pxCanvasWidth - lineLeftRect.left,
            centerPoint.y,
        )
        serifRect.set(
            inclineRect.left,
            centerPoint.y,
            pxSerifSize + pxCanvasPadding,
            centerPoint.y,
        )
        textPoint.set(
            centerPoint.x,
            pxCanvasHeight - pxCanvasRadius * 0.5F - pxCanvasPadding * 0.5F
        )

        iconBitmap = createBitmap()
        iconBitmap?.let {
            bitmapMatrix.setTranslate(
                pxCanvasCenter - it.width * 0.5F + pxCanvasPadding,
                pxCanvasCenter - it.height + pxCanvasPadding
            )
        }
        setMeasuredDimension(pxCanvasWidth, pxCanvasHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            save()
            drawBackground()
            drawStroke()
            drawData()
            restore()
        }
    }

    fun setIcon(@DrawableRes iconRes: Int) = apply {
        this.iconRes = iconRes
    }

    fun setGradientMode(gradientMode: GradientMode) = apply {
        this.gradientMode = gradientMode
    }

    fun setScaleMode(scaleMode: ScaleMode) = apply {
        this.scaleMode = scaleMode
    }

    fun isInverted(isEnable: Boolean) = apply {
        isInverted = isEnable
    }

    fun isNightMode(isEnable: Boolean) = apply {
        isNightMode = isEnable
        // TODO: need create night mode
        requestLayout()
    }

    fun setAngle(degree: Double) {
        this.degree = when (scaleMode) {
            ScaleMode.NONE -> degree
            ScaleMode.LINEAR -> {
                val scale =
                    if (degree.absoluteValue < 1.0) 0.0 else ceil(25 * degree.absoluteValue / 29.0 + 360 / 29.0)
                if (degree < 0) scale.unaryMinus() else scale
            }
            ScaleMode.LOGARITHMIC -> {
                val scale = log((degree.absoluteValue - 90).absoluteValue, 3.0).roundToInt()
                (degree * scale).coerceIn(-90.0..90.0)
            }
        }.let { if (isInverted) it.unaryMinus() else it }.toFloat()

        degreeText = "%.1f\u00BA".format((degree * 10).roundToInt() / 10.0)

        backgroundPaint.color = when (gradientMode) {
            GradientMode.FRONT -> getColorGradient(degree, 0.5, 2.0)
            GradientMode.SIDE -> getColorGradient(degree, 0.8, 4.5)
        }
        invalidate()
    }

    private fun Canvas.drawBackground() {
        drawCircle(centerPoint.x, centerPoint.y, pxCanvasRadius, backgroundPaint)
        drawArc(inclineRect, degree, 180F, true, inclineBackgroundPaint)
    }

    private fun Canvas.drawStroke() {

        fun Canvas.drawSerif() {
            repeat(4) {
                drawLine(serifRect.left, serifRect.top, serifRect.right, serifRect.bottom, serifPaint)
                rotate(5F, centerPoint.x, centerPoint.y)
            }
        }

        drawLine(lineLeftRect.left, lineLeftRect.top, lineLeftRect.right, lineLeftRect.bottom, centerSerifPaint)
        drawLine(lineRightRect.left, lineRightRect.top, lineRightRect.right, lineRightRect.bottom, centerSerifPaint)

        rotate(-20F, centerPoint.x, centerPoint.y)
        drawSerif()
        rotate(5F, centerPoint.x, centerPoint.y)
        drawSerif()
        rotate(135F, centerPoint.x, centerPoint.y)
        drawSerif()
        rotate(5F, centerPoint.x, centerPoint.y)
        drawSerif()
        rotate(155F, centerPoint.x, centerPoint.y)
        drawCircle(centerPoint.x, centerPoint.y, pxCanvasRadius, strokePaint)
    }

    private fun Canvas.drawData() {
        drawText(degreeText, textPoint.x, textPoint.y, textPaint)

        rotate(degree, centerPoint.x, centerPoint.y)
        iconBitmap?.let {
            drawBitmap(it, bitmapMatrix, bitmapPaint)
        }
    }

    private fun createPaint(colorString: String? = null): Paint =
        Paint().apply {
            color = colorString?.let { Color.parseColor(it) } ?: Color.BLACK
            isAntiAlias = true
            isFilterBitmap = true
        }

    private fun createBitmap(@ColorInt color: Int? = null): Bitmap? {
        if (pxBitmapSize == 0) return null
        val drawable = ContextCompat.getDrawable(context, iconRes)?.apply {
            setTint(color ?: Color.BLACK)
        }
        return drawable?.toBitmap(pxBitmapSize, pxBitmapSize)
    }

    @ColorInt
    private fun getColorGradient(degrees: Double, min: Double, max: Double): Int =
        when {
            degrees.absoluteValue >= 0 && degrees.absoluteValue < min -> Color.parseColor("#90EE90")
            degrees.absoluteValue in min..max -> Color.parseColor("#FFD700")
            else -> Color.parseColor("#FF6347")
        }

    enum class ScaleMode {
        NONE,
        LINEAR,
        LOGARITHMIC
    }

    enum class GradientMode {
        FRONT,
        SIDE
    }

    private companion object {
        const val STROKE_WIDTH_SCALE = 0.015
        const val SERIF_WIDTH_SCALE = 0.015
        const val CENTER_SERIF_WIDTH_SCALE = 0.030
        const val SERIF_SCALE = 0.05
        const val TEXT_SCALE = 0.25
        const val BITMAP_SCALE = 0.40
    }
}