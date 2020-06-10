package com.pj.playground.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.pj.playground.R

class ClippedView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private val textsize = resources.getDimension(R.dimen.textSize)
    private val textOffset = resources.getDimension(R.dimen.textOffset)

    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = textsize
    }

    private val path = Path()

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)

    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackAndUnclippedRectangle(canvas)
        drawDifferentClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawSkewedTextExample(canvas)
        drawTranslatedTextExample(canvas)
        // drawQuickRejectExample(canvas)
    }

    private fun drawClippedRectangle(canvas: Canvas) {
        // Set the boundaries of the clipping rectangle for the whole shape.
        // Apply a clipping rectangle that constrains to drawing only the square.
        // The Canvas.clipRect(left, top, right, bottom) method reduces the region
        // of the screen that future draw operations can write to. It sets the clipping
        // boundaries to be the spatial intersection of the current clipping rectangle
        // and the rectangle passed into clipRect().
        canvas.clipRect(
            clipRectLeft, clipRectTop,
            clipRectRight, clipRectBottom
        )

        canvas.drawColor(Color.WHITE)

        // Draw Red diagonal line
        paint.color = Color.RED
        canvas.drawLine(
            clipRectLeft, clipRectTop,
            clipRectRight, clipRectBottom,
            paint
        )

        // Draw Green color circle
        paint.color = Color.GREEN
        canvas.drawCircle(
            circleRadius, clipRectBottom - circleRadius,
            circleRadius, paint
        )

        // Draw blue text, align right(left) to right boundary of rectangle
        // Alight Right actually draws left to the origin
        paint.color = Color.BLUE
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            context.getString(R.string.clipping),
            clipRectRight, textOffset, paint
        )
    }

    private fun drawTranslatedTextExample(canvas: Canvas) {

    }

    private fun drawSkewedTextExample(canvas: Canvas) {

    }

    private fun drawOutsideClippingExample(canvas: Canvas) {

    }

    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {

    }

    private fun drawCombinedClippingExample(canvas: Canvas) {

    }

    private fun drawIntersectionClippingExample(canvas: Canvas) {

    }

    private fun drawCircularClippingExample(canvas: Canvas) {

    }

    private fun drawDifferentClippingExample(canvas: Canvas) {

    }

    private fun drawBackAndUnclippedRectangle(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
        canvas.save()
        canvas.translate(columnOne, rowOne)
        drawClippedRectangle(canvas)
        canvas.restore()
    }
}