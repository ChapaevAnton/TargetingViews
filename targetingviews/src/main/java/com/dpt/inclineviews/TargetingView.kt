package com.dpt.inclineviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin


class TargetingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var pxStrokeWidth = 2F
    private var pxWidthCanvas = 0F
    private var pxHeightCanvas = 0F
    private var pxPaddingCanvas = 0F
    private var pxWidthRect = 0F
    private var pxHeightRect = 0F
    private var pxTextSize = 18F
    private var pxTextLegendSize = 15F
    private var pxBitmapSize = 25
    private var pxAimSize = 2.7F

    private val backgroundColor = Color.parseColor("#FFFFFF")
    private val externalColor = Color.parseColor("#FF6347")
    private val middleColor = Color.parseColor("#FFD700")
    private val centralColor = Color.parseColor("#90EE90")

    private val backgroundPaint = createPaint(backgroundColor).apply {
        style = Paint.Style.FILL
    }
    private val externalCirclePaint = createPaint(externalColor).apply {
        style = Paint.Style.FILL
    }
    private val middleCirclePaint = createPaint(middleColor).apply {
        style = Paint.Style.FILL
    }
    private val centerCirclePaint = createPaint(centralColor).apply {
        style = Paint.Style.FILL
    }
    private val pointCirclePaint = createPaint(backgroundColor).apply {
        style = Paint.Style.FILL
    }
    private val centralPointCirclePaint = createPaint(centralColor).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = createPaint().apply {
        style = Paint.Style.STROKE
        strokeWidth = pxStrokeWidth
    }
    private val strokePointPaint = createPaint().apply {
        style = Paint.Style.STROKE
        strokeWidth = pxStrokeWidth
    }
    private val textTitlePaint = createPaint().apply {
        textSize = pxTextSize
        textAlign = Paint.Align.CENTER
    }
    private val textPaint = createPaint().apply {
        textSize = pxTextSize
        textAlign = Paint.Align.LEFT
    }
    private val textLegendPaint = createPaint().apply {
        textSize = pxTextLegendSize
        textAlign = Paint.Align.LEFT
    }
    private val bitmapPaint = createPaint()

    private var pxExternalAimRadius = 0F
    private var pxMiddleAimRadius = 0F
    private var pxCentralAimRadius = 0F
    private var pxExternalTargetRadius = 0F
    private var pxCentralTargetRadius = 0F

    private val canvasRect = RectF()
    private val targetPoint = PointF()
    private val aimPoint = PointF()
    private val titleRect = RectF()
    private val depthRect = RectF()
    private val angleRect = RectF()
    private val azimuthRect = RectF()
    private val xRect = RectF()
    private val yRect = RectF()
    private val distanceRect = RectF()
    private val redRect = RectF()
    private val yellowRect = RectF()
    private val greenRect = RectF()
    private val redRectBackground = RectF()
    private val yellowRectBackground = RectF()
    private val greenRectBackground = RectF()

    private val textTitlePoint = PointF()
    private val textDepthPoint = PointF()
    private val textAnglePoint = PointF()
    private val textDistancePoint = PointF()
    private val textXPoint = PointF()
    private val textYPoint = PointF()
    private val textAzimuthPoint = PointF()
    private val textRedPoint = PointF()
    private val textYellowPoint = PointF()
    private val textGreenPoint = PointF()

    private val bitmapDepthPoint = PointF()
    private val bitmapAnglePoint = PointF()
    private val bitmapDistancePoint = PointF()
    private val bitmapXPoint = PointF()
    private val bitmapYPoint = PointF()
    private val bitmapAzimuthPoint = PointF()

    private val defaultBitmap =
        Bitmap.createBitmap(pxBitmapSize, pxBitmapSize, Bitmap.Config.ARGB_8888)

    private var leftRes: Int = R.drawable.ic_view_left
    private var topRes: Int = R.drawable.ic_view_top
    private var rightRes: Int = R.drawable.ic_view_right
    private var bottomRes: Int = R.drawable.ic_view_bottom
    private var turnLeftRes: Int = R.drawable.ic_view_turn_left
    private var turnRightRes: Int = R.drawable.ic_view_turn_right
    private var centerRes: Int = R.drawable.ic_view_center
    private var outsideRes: Int = R.drawable.ic_view_outside
    private var depthRes: Int = R.drawable.ic_view_depth
    private var angleRes: Int = R.drawable.ic_view_angle
    private var distanceRes: Int = R.drawable.ic_view_distance

    private lateinit var leftIcon: Bitmap
    private lateinit var topIcon: Bitmap
    private lateinit var rightIcon: Bitmap
    private lateinit var bottomIcon: Bitmap
    private lateinit var turnLeftIcon: Bitmap
    private lateinit var turnRightIcon: Bitmap
    private lateinit var centerIcon: Bitmap
    private lateinit var outsideIcon: Bitmap
    private lateinit var depthIcon: Bitmap
    private lateinit var angleIcon: Bitmap
    private lateinit var distanceIcon: Bitmap

    private lateinit var bitmapDepth: Bitmap
    private lateinit var bitmapAngle: Bitmap
    private lateinit var bitmapAzimuth: Bitmap
    private lateinit var bitmapX: Bitmap
    private lateinit var bitmapY: Bitmap
    private lateinit var bitmapDistance: Bitmap

    private var titleText = "title"
    private var depthText = "0,0м"
    private var angleText = "0º"
    private var azimuthText: String? = "0º"
    private var xText: String? = "0см"
    private var yText: String? = "0см"
    private var distanceText = "0см"

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthExpect = paddingLeft + paddingRight + VIEW_SIZE
        val heightExpect = paddingTop + paddingBottom + VIEW_SIZE
        val widthResolve = resolveSize(widthExpect, widthMeasureSpec)
        val heightResolve = resolveSize(heightExpect, heightMeasureSpec)
        val side = min(widthResolve, heightResolve)
        val paddingLeftRight = min(paddingLeft, paddingRight)
        val paddingTopBottom = min(paddingTop, paddingBottom)

        val pxMarginCanvas = side * CANVAS_MARGIN_SCALE
        pxStrokeWidth = side * STROKE_WIDTH_SCALE
        pxWidthCanvas = side + pxStrokeWidth * 2
        pxHeightCanvas = side + pxStrokeWidth + pxMarginCanvas
        // FIXME: pxPaddingCanvas not yet applied
        pxPaddingCanvas =
            min(paddingLeftRight, paddingTopBottom).toFloat()
        pxWidthRect = side * RECT_WIDTH_SCALE
        pxHeightRect = side * RECT_HEIGHT_SCALE
        pxTextSize = side * TEXT_SIZE_SCALE
        pxTextLegendSize = side * TEXT_SIZE_LEGEND_SCALE
        pxBitmapSize = (side * BITMAP_SIZE_SCALE).toInt()
        pxAimSize = side * AIM_SCALE

        val radius = (side - pxPaddingCanvas * 2) * 0.5F
        canvasRect.set(
            -radius + pxStrokeWidth,
            -radius - pxMarginCanvas + pxStrokeWidth,
            radius - pxStrokeWidth,
            radius - pxStrokeWidth
        )
        pxExternalAimRadius = radius
        pxMiddleAimRadius = radius * MIDDLE_RADIUS_SCALE
        pxCentralAimRadius = radius * CENTRAL_RADIUS_SCALE
        pxExternalTargetRadius = radius * POINT_RADIUS_SCALE
        pxCentralTargetRadius = radius * CENTRAL_POINT_RADIUS_SCALE

        strokePaint.strokeWidth = pxStrokeWidth
        strokePointPaint.strokeWidth = pxStrokeWidth
        textTitlePaint.textSize = pxTextSize
        textPaint.textSize = pxTextSize
        textLegendPaint.textSize = pxTextLegendSize

        aimPoint.set(
            radius + pxStrokeWidth,
            radius + pxMarginCanvas
        )
        titleRect.set(
            canvasRect.left,
            canvasRect.top,
            canvasRect.right,
            canvasRect.top + pxHeightRect
        )
        textTitlePoint.set(0F, titleRect.top + pxStrokeWidth + pxTextSize)

        fun measureCell(
            left: Float,
            top: Float,
            rect: RectF,
            bitmap: PointF,
            text: PointF,
            isFirst: Boolean = false
        ) {
            val l = left + pxStrokeWidth * if (isFirst) 0 else 2
            val t = top + pxStrokeWidth * 2
            val r = l + pxWidthRect
            val b = t + pxHeightRect
            rect.set(l, t, r, b)
            bitmap.set(
                rect.left + pxStrokeWidth * 0.5F,
                rect.top + pxStrokeWidth * 1.5F
            )
            text.set(
                bitmap.x + pxBitmapSize,
                rect.top + pxStrokeWidth + pxTextSize
            )
        }

        measureCell(
            left = canvasRect.left,
            top = titleRect.bottom,
            rect = depthRect,
            bitmap = bitmapDepthPoint,
            text = textDepthPoint,
            isFirst = true
        )
        measureCell(
            left = depthRect.right,
            top = titleRect.bottom,
            rect = angleRect,
            bitmap = bitmapAnglePoint,
            text = textAnglePoint
        )
        measureCell(
            left = angleRect.right,
            top = titleRect.bottom,
            rect = distanceRect,
            bitmap = bitmapDistancePoint,
            text = textDistancePoint
        )
        measureCell(
            left = canvasRect.left,
            top = depthRect.bottom,
            rect = xRect,
            bitmap = bitmapXPoint,
            text = textXPoint,
            isFirst = true
        )
        measureCell(
            left = xRect.right,
            top = angleRect.bottom,
            rect = yRect,
            bitmap = bitmapYPoint,
            text = textYPoint
        )
        measureCell(
            left = yRect.right,
            top = distanceRect.bottom,
            rect = azimuthRect,
            bitmap = bitmapAzimuthPoint,
            text = textAzimuthPoint
        )

        fun measureCell(
            left: Float,
            top: Float,
            right: Float,
            rect: RectF,
            background: RectF,
            text: PointF,
            isFirst: Boolean = false
        ) {
            val l = left + pxStrokeWidth * if (isFirst) 0 else 2
            val t = top + pxStrokeWidth * 2
            val r = rect.left + pxHeightRect
            val b = rect.top + pxHeightRect * 0.6F
            rect.set(l, t, r, b)
            background.set(l, t, right, b)
            text.set(
                rect.right + pxStrokeWidth,
                rect.top + pxTextLegendSize
            )
        }

        measureCell(
            left = canvasRect.left,
            top = xRect.bottom,
            right = xRect.right,
            rect = redRect,
            background = redRectBackground,
            text = textRedPoint,
            isFirst = true
        )
        measureCell(
            left = xRect.right,
            top = yRect.bottom,
            right = yRect.right,
            rect = yellowRect,
            background = yellowRectBackground,
            text = textYellowPoint
        )
        measureCell(
            left = yRect.right,
            top = azimuthRect.bottom,
            right = distanceRect.right,
            rect = greenRect,
            background = greenRectBackground,
            text = textGreenPoint
        )

        leftIcon = createBitmap(leftRes)
        topIcon = createBitmap(topRes)
        rightIcon = createBitmap(rightRes)
        bottomIcon = createBitmap(bottomRes)
        turnLeftIcon = createBitmap(turnLeftRes)
        turnRightIcon = createBitmap(turnRightRes)
        centerIcon = createBitmap(centerRes)
        outsideIcon = createBitmap(outsideRes)
        depthIcon = createBitmap(depthRes)
        angleIcon = createBitmap(angleRes)
        distanceIcon = createBitmap(distanceRes)

        bitmapDepth = depthIcon
        bitmapAngle = angleIcon
        bitmapAzimuth = centerIcon
        bitmapX = centerIcon
        bitmapY = centerIcon
        bitmapDistance = distanceIcon

        setMeasuredDimension(pxWidthCanvas.toInt(), pxHeightCanvas.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            save()
            //aim
            translate(aimPoint.x, aimPoint.y)
            drawCircle(0F, 0F, pxExternalAimRadius, externalCirclePaint)
            drawCircle(0F, 0F, pxExternalAimRadius, strokePaint)
            drawCircle(0F, 0F, pxMiddleAimRadius, middleCirclePaint)
            drawCircle(0F, 0F, pxMiddleAimRadius, strokePaint)
            drawLine(pxExternalAimRadius, 0F, -pxExternalAimRadius, 0F, strokePaint)
            drawLine(0F, pxExternalAimRadius, 0F, -pxExternalAimRadius, strokePaint)
            drawCircle(0F, 0F, pxCentralAimRadius, centerCirclePaint)
            drawCircle(0F, 0F, pxCentralAimRadius, strokePaint)
            //target
            drawCircle(targetPoint.x, targetPoint.y, pxExternalTargetRadius, pointCirclePaint)
            drawCircle(targetPoint.x, targetPoint.y, pxExternalTargetRadius, strokePointPaint)
            drawCircle(targetPoint.x, targetPoint.y, pxCentralTargetRadius, centralPointCirclePaint)
            drawCircle(targetPoint.x, targetPoint.y, pxCentralTargetRadius, strokePointPaint)
            //table
            drawRect(titleRect, backgroundPaint)
            drawRect(titleRect, strokePaint)
            drawRect(depthRect, backgroundPaint)
            drawRect(depthRect, strokePaint)
            drawRect(angleRect, backgroundPaint)
            drawRect(angleRect, strokePaint)
            drawRect(distanceRect, backgroundPaint)
            drawRect(distanceRect, strokePaint)
            drawRect(xRect, backgroundPaint)
            drawRect(xRect, strokePaint)
            drawRect(redRectBackground, backgroundPaint)
            drawRect(redRect, externalCirclePaint)
            drawText("100см", textRedPoint.x, textRedPoint.y, textLegendPaint)
            drawRect(yRect, backgroundPaint)
            drawRect(yRect, strokePaint)
            drawRect(yellowRectBackground, backgroundPaint)
            drawRect(yellowRect, middleCirclePaint)
            drawText("65см", textYellowPoint.x, textYellowPoint.y, textLegendPaint)
            drawRect(azimuthRect, backgroundPaint)
            drawRect(azimuthRect, strokePaint)
            drawRect(greenRectBackground, backgroundPaint)
            drawRect(greenRect, centerCirclePaint)
            drawText("30см", textGreenPoint.x, textGreenPoint.y, textLegendPaint)
            //icon
            drawBitmap(bitmapDepth, bitmapDepthPoint.x, bitmapDepthPoint.y, bitmapPaint)
            drawBitmap(bitmapAngle, bitmapAnglePoint.x, bitmapAnglePoint.y, bitmapPaint)
            drawBitmap(bitmapAzimuth, bitmapAzimuthPoint.x, bitmapAzimuthPoint.y, bitmapPaint)
            drawBitmap(bitmapX, bitmapXPoint.x, bitmapXPoint.y, bitmapPaint)
            drawBitmap(bitmapY, bitmapYPoint.x, bitmapYPoint.y, bitmapPaint)
            drawBitmap(bitmapDistance, bitmapDistancePoint.x, bitmapDistancePoint.y, bitmapPaint)
            //data
            drawText(titleText, textTitlePoint.x, textTitlePoint.y, textTitlePaint)
            drawText(depthText, textDepthPoint.x, textDepthPoint.y, textPaint)
            drawText(angleText, textAnglePoint.x, textAnglePoint.y, textPaint)
            drawText(azimuthText ?: "º", textAzimuthPoint.x, textAzimuthPoint.y, textPaint)
            drawText(xText ?: "см", textXPoint.x, textXPoint.y, textPaint)
            drawText(yText ?: "см", textYPoint.x, textYPoint.y, textPaint)
            drawText(distanceText, textDistancePoint.x, textDistancePoint.y, textPaint)
            restore()
        }
    }

    fun setDirectionsIcon(
        @DrawableRes leftRes: Int,
        @DrawableRes topRes: Int,
        @DrawableRes rightRes: Int,
        @DrawableRes bottomRes: Int,
        @DrawableRes turnLeftRes: Int,
        @DrawableRes turnRightRes: Int,
        @DrawableRes centerRes: Int,
        @DrawableRes outsideRes: Int,
    ) = apply {
        this.leftRes = leftRes
        this.topRes = topRes
        this.rightRes = rightRes
        this.bottomRes = bottomRes
        this.turnLeftRes = turnLeftRes
        this.turnRightRes = turnRightRes
        this.centerRes = centerRes
        this.outsideRes = outsideRes
    }

    fun setDataIcon(
        @DrawableRes depthRes: Int,
        @DrawableRes angleRes: Int,
        @DrawableRes distanceRes: Int
    ) = apply {
        this.depthRes = depthRes
        this.angleRes = angleRes
        this.distanceRes = distanceRes
    }

    fun setPoint(
        title: String,
        depth: Double,
        angle: Double,
        mX: Double,
        mY: Double,
        distance: Double,
        heading: Double,
        azimuth: Double?
    ) {
        titleText = title
        depthText = "%.1fм".format(depth)
        angleText = "%d\u00BA".format(angle.roundToInt())

        val x = mX * cos(Math.toRadians(-heading)) + mY * sin(Math.toRadians(-heading))
        val y = -mX * sin(Math.toRadians(-heading)) + mY * cos(Math.toRadians(-heading))

        if (inTargetArea(x, y)) {
            renderXY(x, -y)
            renderTarget(x, -y)
        } else {
            resetRender()
        }
        renderAzimuth(azimuth)
        renderDistance(distance)

        Log.d(TAG, "x $xText")
        Log.d(TAG, "y $yText")
        Log.d(TAG, "azimuth $azimuthText")
        Log.d(TAG, "distance $distanceText")

        invalidate()
    }

    private fun inTargetArea(mX: Double, mY: Double): Boolean {
        return getAimParam(mX, mY).let {
            it.targetRadius <= it.externalAimRadius
        }
    }

    private fun renderXY(mX: Double, mY: Double) {
        xText = "%dсм".format((abs(mX) * 100).toInt())
        yText = "%dсм".format((abs(mY) * 100).toInt())
        bitmapX =
            if (mX > 0) rightIcon else if (mX < 0) leftIcon else centerIcon
        bitmapY =
            if (mY > 0) bottomIcon else if (mY < 0) topIcon else centerIcon
    }

    private fun renderTarget(mX: Double, mY: Double) {
        getAimParam(mX, mY).apply {
            targetPoint.set(point)
            centralPointCirclePaint.color = when {
                targetRadius > middleAimRadius && targetRadius <= externalAimRadius -> externalColor
                targetRadius > centralAimRadius && targetRadius <= middleAimRadius -> middleColor
                targetRadius in 0F..centralAimRadius -> centralColor
                else -> Color.BLACK
            }
        }
        strokePointPaint.color = Color.BLACK
        pointCirclePaint.color = Color.WHITE
    }

    // TODO: азимут должен отображаться только в зоне 2.9 метра
    private fun renderAzimuth(azimuth: Double?) {
        if (azimuth != null) {
            azimuthText = "%d\u00BA".format(abs(azimuth).toInt())
            bitmapAzimuth =
                if (azimuth.toInt() > 0) {
                    turnRightIcon
                } else if (azimuth.toInt() < 0) {
                    turnLeftIcon
                } else {
                    centerIcon
                }
        } else {
            azimuthText = null
            bitmapAzimuth = outsideIcon
        }
    }

    private fun renderDistance(distance: Double) {
        distanceText =
            if (distance >= 1000) {
                "%.1fкм".format((distance / 1000 * 100).roundToInt() / 100.0F)
            } else if (distance >= 1.0) {
                "%.1fм".format((distance * 100).roundToInt() / 100.0F)
            } else {
                "%.0fсм".format((distance * 100).toInt().toFloat())
            }
    }

    private fun resetRender() {
        xText = null
        yText = null
        bitmapX = outsideIcon
        bitmapY = outsideIcon

        azimuthText = null
        bitmapAzimuth = outsideIcon

        centralPointCirclePaint.color = Color.TRANSPARENT
        strokePointPaint.color = Color.TRANSPARENT
        pointCirclePaint.color = Color.TRANSPARENT
    }

    private fun getAimParam(mX: Double, mY: Double): AimParam {
        val point = PointF(
            (mX * pxAimSize * 0.5 / AIM_SCALE).toFloat(),
            (mY * pxAimSize * 0.5 / AIM_SCALE).toFloat()
        )
        val targetRadius = (0F - point.x).pow(2) + (0F - point.y).pow(2)
        val externalRadius = pxExternalAimRadius.pow(2)
        val middleRadius = pxMiddleAimRadius.pow(2)
        val centralRadius = pxCentralAimRadius.pow(2)
        return AimParam(
            point,
            targetRadius,
            externalRadius,
            middleRadius,
            centralRadius
        )
    }

    private fun createPaint(@ColorInt colorInt: Int? = null): Paint =
        Paint().apply {
            color = colorInt ?: Color.BLACK
            isAntiAlias = true
            isFilterBitmap = true
        }

    private fun createBitmap(
        @DrawableRes drawableInt: Int? = null
    ): Bitmap =
        drawableInt?.let {
            ContextCompat.getDrawable(context, it)?.toBitmap(pxBitmapSize, pxBitmapSize)
        } ?: defaultBitmap

    private data class AimParam(
        val point: PointF,
        val targetRadius: Float,
        val externalAimRadius: Float,
        val middleAimRadius: Float,
        val centralAimRadius: Float
    )

    private companion object {
        //350 - 8pro
        //270 - 10pro
        const val VIEW_SIZE = 270//100 cm
        const val AIM_SCALE = 0.01F //2.7px - 1 cm
        const val CANVAS_MARGIN_SCALE = 0.50F
        const val STROKE_WIDTH_SCALE = 0.0085F
        const val MIDDLE_RADIUS_SCALE = 0.65F
        const val CENTRAL_RADIUS_SCALE = 0.3F
        const val POINT_RADIUS_SCALE = 0.171F
        const val CENTRAL_POINT_RADIUS_SCALE = 0.05F
        const val BITMAP_SIZE_SCALE = 0.0916F
        const val TEXT_SIZE_SCALE = 0.066F
        const val TEXT_SIZE_LEGEND_SCALE = 0.055F
        const val RECT_HEIGHT_SCALE = 0.116F
        const val RECT_WIDTH_SCALE = 0.316F

        val TAG = TargetingView::class.simpleName
    }
}