package com.example.obliquelitterkotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class AnimatedOvalView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = context.getColor(R.color.purple_200) // Your color here
        style = Paint.Style.FILL
    }

    private val rect = RectF()
    var xPos = 0f
    var yPos = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Set the position of the oval based on xPos and yPos
        rect.set(xPos - 25, yPos - 25, xPos + 25, yPos + 25) // The oval size, change as needed

        canvas.drawOval(rect, paint)
    }

    fun moveToPoint(x: Float, y: Float) {
        xPos = x
        yPos = y

        // Invalidate to redraw the view
        invalidate()
    }
}
