package com.example.drawme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attr: AttributeSet): View(context, attr) {

    private var mDrawPath : CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null // An instance of the bitmap
    private var mDrawPaint: Paint? = null // The paint class
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat() // A variable for stroke/brush size to draw on the canvas.
    private var color = Color.BLACK
    private var canvas: Canvas? = null

    init {
        setUpDrawing() //code inside init will be
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)//bitmap configuration
        canvas = Canvas(mCanvasBitmap!!) // set canvas as mCanvasBitmap

    }
        // Canvas? to canvas if fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f,mCanvasPaint)

            if(!mDrawPath!!.isEmpty) {
                mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness //set the brush thickness
                mDrawPaint!!.color = mDrawPath!!.color //set the color
                canvas.drawPath(mDrawPath!!, mDrawPaint!!)
            }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean { //what happen when we touch

        val touchx = event?.x
        val touchy = event?.y

        when (event?.action){

        }

        return super.onTouchEvent(event)
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path(){



    }

}