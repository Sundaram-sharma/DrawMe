package com.example.drawme

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permission -> //types of permission
            permission.entries.forEach{
                //mutable mapping
                val permissionName = it.key //this will be of type string
                val isGranted = it.value// this will be of type bool

                if(isGranted){ //if the permission is granted
                    Toast.makeText(this@MainActivity," Permission granted, now you can read from file storage.", Toast.LENGTH_LONG).show()
                }else{//Make manifest to import android library instead of java
                    if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE)//if the permission is not granted
                    {
                        Toast.makeText(this@MainActivity,"Oops you just denied the permission",Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

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
        val ib_brush : ImageButton = findViewById(R.id.ib_brush) //data
        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        val ibGallery : ImageButton = findViewById(R.id.ib_gallery) //when we clikc on gallery icon, the inside functions will launch
        ibGallery.setOnClickListener{

            requestStoragePermission()

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

        brushDialog.show()    //show the brush dialog
    }

    private fun requestStoragePermission(){
        //Todo 6: Check if the permission was denied and show rationale
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        ){
            //Todo 9: call the rationale dialog to tell the user why they need to allow permission request
            showRationaleDialog("Kids Drawing App","Kids Drawing App " +
                    "needs to Access Your External Storage")
        }
        else {
            // You can directly ask for the permission.
            // Todo 7: if it has not been denied then request for permission

            //  The registered ActivityResultCallback gets the result of this request.
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

    }

    private fun showRationaleDialog(
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

}