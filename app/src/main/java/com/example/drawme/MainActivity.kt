package com.example.drawme

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null

    //create an activity result launcher to open an intent
    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){//registerForActivityResult gives us the data
            result ->
            //get the returned result from the lambda and check the resultcode and the data returned
            if(result.resultCode == RESULT_OK && result.data!=null){
                //process the data
                //if the data is not null reference the imageView from the layout
                val imageBackground: ImageView = findViewById(R.id.iv_background) // to get the image to the main view
                //set the imageuri received
                imageBackground.setImageURI(result.data?.data)
            }
        }


    /** create an ActivityResultLauncher with MultiplePermissions since we are requesting
     * both read and write
     */

    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permission -> //types of permission
            permission.entries.forEach{
                //mutable mapping
                val permissionName = it.key //this will be of type string
                val isGranted = it.value// this will be of type bool

                //if permission is granted show a toast and perform operation

                if(isGranted){ //if the permission is granted
                    Toast.makeText(this@MainActivity," Permission granted, now you can read from file storage.", Toast.LENGTH_LONG).show()

                    //perform operation
                    //create an intent to pick image from external storage

                    val pickIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    //using the intent launcher created above launch the pick intent
                    openGalleryLauncher.launch(pickIntent) //open the gallery after clicking ib_galery button
                }else{//Make manifest to import android library instead of java
                    //Displaying another toast if permission is not granted and this time focus on
                    //    Read external storage
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
        drawingView?.setSizeForBrush(10.toFloat()) //Default Size is 20

        val linearLayoutPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colors) //the linear layout where the color pillet will be

        mImageButtonCurrentPaint = linearLayoutPaintColors[0] as ImageButton // we need to use the item at position one in the color pallet of linear layout
        mImageButtonCurrentPaint!!.setImageDrawable(// when we please a color in the pallet
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )
        val ib_brush : ImageButton = findViewById(R.id.ib_brush) //data
        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        val ib_Gallery : ImageButton = findViewById(R.id.ib_gallery) //when we clikc on gallery icon, the inside functions will launch
        ib_Gallery.setOnClickListener{

            requestStoragePermission()

        }

        val ib_Undo : ImageButton = findViewById(R.id.ib_undo) //when clicked, it will undo the previous work
        ib_Undo.setOnClickListener{
            drawingView?.onClickUndo() //calling the function from DrawingView class (nullable)
        }

        val ib_Redo : ImageButton = findViewById(R.id.ib_redo) //when clicked, it will Redo the Undo work
        ib_Redo.setOnClickListener{
            drawingView?.onClickRedo() //calling the function from DrawingView class (nullable)
        }

        val ib_Save : ImageButton = findViewById(R.id.ib_save) //when clicked, it will save the image
        ib_Save.setOnClickListener{
            //user permission is needed to take permission to write the file on the disk

            if(isReadStorageAllowed()){
                lifecycleScope.launch{
                    val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)//tahes the whole framelayout
                    //val myBitmap = Bitmap = getBitmapFromView(flDrawingView)
                    saveBitmapFile(getBitmapFromView(flDrawingView)) //did the same as above line
                }
            }

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

    //create a method to requestStorage permission

    private fun requestStoragePermission(){
        // Check if the permission was denied and show rationale
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        ){
            //call the rationale dialog to tell the user why they need to allow permission request
            showRationaleDialog("Kids Drawing App","Kids Drawing App " +
                    "needs to Access Your External Storage")
        }
        else {
            // You can directly ask for the permission.
            //if it has not been denied then request for permission

            //  The registered ActivityResultCallback gets the result of this request.
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

    }

    private fun isReadStorageAllowed(): Boolean{
        //check if the user permission is granted
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)

        //return if the permission is granted
        return result == PackageManager.PERMISSION_GRANTED
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

    private fun getBitmapFromView(view: View): Bitmap{ //create a function which takes view in parameter and returns bitmap
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)//takes view width and height and ARBG and opacity
        val canvas = Canvas(returnedBitmap)//Construct a canvas with the specified bitmap to draw into
        val bgDrawable = view.background // takes the background that we defined in the view
        if(bgDrawable != null ){
            bgDrawable.draw(canvas) // if custom background provided then draw the current 'canvas as well'

        }else{
            canvas.drawColor(Color.WHITE) //if no background then provide the canvas with white color
        }
        view.draw(canvas)//view the canvas final
        return returnedBitmap

    }
        //we need to add dependencies in gradle to implement coroutines
    private suspend fun saveBitmapFile (mBitmap: Bitmap) : String{
        var result =""
            //defined the name of the thread
            withContext(Dispatchers.IO){
                if(mBitmap != null ){// run only when mBitmap have something
                     try {
                         val bytes = ByteArrayOutputStream()
                         //fun compress the bitmap to the specified quality in the parameter
                         mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                         //Name and path(location) of the file
                         val f = File(externalCacheDir?.absoluteFile.toString()+File.separator+"DrawMe_"+System.currentTimeMillis()/1000+".png")

                         val fo = FileOutputStream(f) //creating the File Output stream
                         fo.write(bytes.toByteArray()) // writing something on it
                         fo.close() //closing the FO stream

                         result = f.absolutePath //return is the path of the file

                         runOnUiThread{
                             if(result.isNotEmpty()) //saved successfully on the result
                             {
                                 Toast.makeText(this@MainActivity,"File saved successfully :$result",Toast.LENGTH_SHORT).show()

                             }
                         }
                     }catch (e: Exception){
                         result = ""
                         e.printStackTrace()
                     }
                }

            }
            return result //return is the path of the file
    }

}