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
    private val mPath = ArrayList<CustomPath>() // to retain the path we draw

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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { //Called when the size of this view has changed.
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)//bitmap configuration
        canvas = Canvas(mCanvasBitmap!!) // set canvas as mCanvasBitmap

    }
        // Canvas? to canvas if fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f,mCanvasPaint)

        for(path in mPath){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

            if(!mDrawPath!!.isEmpty) { //mDrawPath is nullable and empty
                mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness //set the brush thickness
                mDrawPaint!!.color = mDrawPath!!.color //set the color
                canvas.drawPath(mDrawPath!!, mDrawPaint!!)
            }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean { //what happen when we touch

        val touchx = event?.x //for the x axis
        val touchy = event?.y //for the y axis

        when (event?.action){ // '!!' for nullable
            MotionEvent.ACTION_DOWN ->{ //when we start touching the screen
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchx!!,touchy!!)


            }
            MotionEvent.ACTION_MOVE ->{ //when we drag on the screen
                mDrawPath!!.lineTo(touchx!!, touchy!!)
            }
            MotionEvent.ACTION_UP ->{ // when we stop touching the screen or pressure gesture is finished
                mPath.add(mDrawPath!!) //
                mDrawPath = CustomPath(color, mBrushSize)
            }

        else -> return false // in none of the above is true
        }
         invalidate() //Invalidate the whole view.

        return true
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path(){



    }

}