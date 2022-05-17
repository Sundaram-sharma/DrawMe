package com.example.drawme

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(20.toFloat())

        val linearLayoutPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colors) //the linear layout where the color pillet will be

        mImageButtonCurrentPaint = linearLayoutPaintColors[0] as ImageButton // we need to use the item at position one in the color pallet of linear layout
        mImageButtonCurrentPaint!!.setImageDrawable(// when we please a color in the pallet
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )
        val ib_brush : ImageButton = findViewById(R.id.ib_brush)
        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }
    }

    fun paintClicked(view: View) {
            if(view !== mImageButtonCurrentPaint){
                val imageButton = view as ImageButton
                val colorTag = imageButton.tag.toString() // assigning the strings from color.xml to colorTag variable
                drawingView?.setColor(colorTag) //passing colorTag as a parameter to select color from drawingView

                imageButton.setImageDrawable(// making the current button pressed
                    ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
                )

                mImageButtonCurrentPaint?.setImageDrawable(// making the unselected button normal
                    ContextCompat.getDrawable(this,R.drawable.pallet_normal)
                )
                mImageButtonCurrentPaint = view // making current
            }
    }


    /**
     * Method is used to launch the dialog to select different brush sizes.
     */
    private fun showBrushSizeChooserDialog(){ //
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())//creating the button that will call the
            brushDialog.dismiss() // close the dialog
        }

        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())//creating the button that will call the
            brushDialog.dismiss() // close the dialog
        }

        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())//creating the button that will call the
            brushDialog.dismiss() // close the dialog
        }

        brushDialog.show() //show the brush dialog
    }


}