package com.pj.playground.view

import android.content.Context
import android.graphics.*
import android.os.Build
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
    private val rejectRow = rowFour + rectInset + 2 * clipRectBottom

    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

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
        drawQuickRejectExample(canvas)
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

    private fun drawDifferentClippingExample(canvas: Canvas) {
        canvas.save()

        // Move the origin to the right for the next rectangle.
        canvas.translate(columnTwo, rowOne)

        // Use the subtraction of two clipping rectangles to create a frame.
        canvas.clipRect(
            2 * rectInset, 2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )

        // The method clipRect(float, float, float, float, Region.Op
        // .DIFFERENCE) was deprecated in API level 26. The recommended
        // alternative method is clipOutRect(float, float, float, float),
        // which is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                4 * rectInset, 4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
                Region.Op.DIFFERENCE
            )
        } else {
            canvas.clipOutRect(
                4 * rectInset, 4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset
            )
        }

        drawClippedRectangle(canvas)

        canvas.restore()
    }

    private fun drawCircularClippingExample(canvas: Canvas) {
        canvas.save()

        canvas.translate(columnOne, rowTwo)

        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()
        path.addCircle(
            circleRadius, clipRectBottom - circleRadius,
            circleRadius, Path.Direction.CCW
        )

        // The method clipPath(path, Region.Op.DIFFERENCE) was deprecated in
        // API level 26. The recommended alternative method is
        // clipOutPath(Path), which is currently available in
        // API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(path)
        }

        drawClippedRectangle(canvas)

        canvas.restore()
    }

    private fun drawIntersectionClippingExample(canvas: Canvas) {
        canvas.save()

        canvas.translate(columnTwo, rowTwo)

        canvas.clipRect(
            clipRectLeft, clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )

        // The method clipRect(float, float, float, float, Region.Op
        // .INTERSECT) was deprecated in API level 26. The recommended
        // alternative method is clipRect(float, float, float, float), which
        // is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight, clipRectBottom,
                Region.Op.INTERSECT
            )
        } else {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight, clipRectBottom
            )
        }

        drawClippedRectangle(canvas)

        canvas.restore()
    }

    private fun drawCombinedClippingExample(canvas: Canvas) {
        canvas.save()

        canvas.translate(columnOne, rowThree)

        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + rectInset + circleRadius,
            circleRadius, Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + rectInset + circleRadius,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset, Path.Direction.CCW
        )
        canvas.clipPath(path)

        drawClippedRectangle(canvas)

        canvas.restore()
    }

    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
        canvas.save()

        canvas.translate(columnTwo, rowThree)

        path.rewind()
        path.addRoundRect(
            rectF, clipRectRight / 4,
            clipRectRight / 4, Path.Direction.CCW
        )
        canvas.clipPath(path)

        drawClippedRectangle(canvas)

        canvas.restore()
    }

    private fun drawOutsideClippingExample(canvas: Canvas) {
        canvas.save()

        canvas.translate(columnOne, rowFour)

        canvas.clipRect(
            2 * rectInset, 2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )

        drawClippedRectangle(canvas)

        canvas.restore()
    }

    private fun drawTranslatedTextExample(canvas: Canvas) {
        canvas.save()

        paint.color = Color.GREEN
        // Align the RIGHT side of the text with the origin.
        paint.textAlign = Paint.Align.LEFT
        // Apply transformation to canvas.
        canvas.translate(columnTwo, textRow)

        // Draw text.
        canvas.drawText(
            context.getString(R.string.translated),
            clipRectLeft, clipRectTop, paint
        )

        canvas.restore()
    }

    private fun drawSkewedTextExample(canvas: Canvas) {
        canvas.save()

        paint.color = Color.YELLOW
        paint.textAlign = Paint.Align.RIGHT

        // Position text.
        canvas.translate(columnTwo, textRow)
        // Apply skew transformation.
        canvas.skew(0.2f, 0.3f)
        canvas.drawText(
            context.getString(R.string.skewed),
            clipRectLeft, clipRectTop, paint
        )

        canvas.restore()
    }

    private fun drawQuickRejectExample(canvas: Canvas) {
        val inClipRectangle = RectF(
            clipRectRight / 2,
            clipRectBottom / 2,
            clipRectRight * 2,
            clipRectBottom * 2
        )

        val notInClipRectangle = RectF(
            RectF(
                clipRectRight + 1,
                clipRectBottom + 1,
                clipRectRight * 2,
                clipRectBottom * 2
            )
        )

        canvas.save()

        canvas.translate(columnOne, rejectRow)

        canvas.clipRect(
            clipRectLeft, clipRectTop,
            clipRectRight, clipRectBottom
        )

        // We can change to `notInClipRectangle` to see the other case
        if (canvas.quickReject(inClipRectangle, Canvas.EdgeType.AA)) {
            canvas.drawColor(Color.WHITE)
        } else {
            canvas.drawColor(Color.BLACK)
            canvas.drawRect(inClipRectangle, paint)
        }

        canvas.restore()
    }

    private fun drawBackAndUnclippedRectangle(canvas: Canvas) {
        canvas.drawColor(Color.GRAY)
        canvas.save()
        canvas.translate(columnOne, rowOne)
        drawClippedRectangle(canvas)
        canvas.restore()
    }
}