package com.omsworld.familycare.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class LineEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val mRect = Rect()
    private val mPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLUE
    }

    override fun onDraw(canvas: Canvas) {
        val height = height
        val lineHeight = lineHeight
        var count = height / lineHeight
        if (lineCount > count) count = lineCount

        val r = mRect
        val paint = mPaint
        var baseline = getLineBounds(0, r)

        for (i in 0 until count) {
            canvas.drawLine(r.left.toFloat(), (baseline + 1).toFloat(),
                r.right.toFloat(), (baseline + 1).toFloat(), paint)
            baseline += lineHeight
            super.onDraw(canvas)
        }
    }
}