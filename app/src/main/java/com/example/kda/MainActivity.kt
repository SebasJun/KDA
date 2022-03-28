package com.example.kda

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null
    val openGalleryLauncer: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK && result.data != null){
            //iv_background
            val imageBackGround: ImageView = findViewById(R.id.iv_background)
            imageBackGround.setImageURI(result.data?.data)
        }
    }
    val requestPermission: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        permissions ->
        permissions.entries.forEach(){
            val permissionName = it.key
            val isGranted = it.value

            if(isGranted){
                val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                openGalleryLauncer.launch(pickIntent)

            }else{
                if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                    Toast.makeText(this,"Not granted", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(10.toFloat())

        val linearLayourPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayourPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        val ibGallery: ImageButton = findViewById(R.id.ib_image_selector)
        val ib_brush: ImageButton = findViewById(R.id.ib_brush)
        val ib_clear : ImageButton = findViewById(R.id.ib_clear)

        ib_brush.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        ib_clear.setOnClickListener{
            drawingView?.clearDrawingBoard()
        }

        ibGallery.setOnClickListener{
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            showRationalDialog("Kids Drawing App", "Kids Drawing App " + "needs to acces your external storage")
        }else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            //TODO: Add write permision
        }
    }

    fun paintClicked(view: View){

        if(view !== mImageButtonCurrentPaint){

            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )

            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view
        }
    }

    private fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size")

        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)

        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        mediumBtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        largeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    private fun showRationalDialog(title: String, message: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title).setMessage(message).setPositiveButton("Cancel"){dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}