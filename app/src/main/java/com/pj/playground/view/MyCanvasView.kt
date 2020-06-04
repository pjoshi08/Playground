package com.pj.playground.view

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.pj.playground.R
import kotlin.math.abs

private const val STROKE_WIDTH = 12f    // has to be float

class MyCanvasView(context: Context) : View(context) {

    // Path representing the drawing so far
    private val drawing = Path()

    // Path representing what's currently being drawn
    private val currentPath = Path()

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is being drawn
        isAntiAlias = true
        // Dithering affects how colors with higher precision than the device are down sampled.
        isDither = true
        style = Paint.Style.STROKE      // Default: FILL
        strokeJoin = Paint.Join.ROUND   // Default: MITER
        strokeCap = Paint.Cap.ROUND     // Default: BUTT
        strokeWidth = STROKE_WIDTH      // Default: Hairline-Width (really thin)
    }

    private var path = Path()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private var touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private lateinit var frame: Rect

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(backgroundColor)

        // Draw the drawing so far
        canvas.drawPath(drawing, paint)

        // Draw any current squiggle
        canvas.drawPath(currentPath, paint)

        // Draw a frame around the canvas.
        canvas.drawRect(frame, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }

        return true
    }

    private fun touchStart() {
        currentPath.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            currentPath.quadTo(
                currentX,
                currentY,
                (currentX + motionTouchEventX) / 2,
                (currentY + motionTouchEventY) / 2
            )

            currentX = motionTouchEventX
            currentY = motionTouchEventY

            // Draw the path in the extra bitmap to cache it.
            //extraCanvas.drawPath(path, paint)
        }

        invalidate()
    }

    private fun touchUp() {
        // Add the current path to the drawing so far
        drawing.addPath(currentPath)

        // Reset the path so it doesn't get drawn again.
        currentPath.reset()
    }
}