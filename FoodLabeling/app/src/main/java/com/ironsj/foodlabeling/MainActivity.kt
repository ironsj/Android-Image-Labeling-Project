package com.ironsj.foodlabeling

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.io.IOException
import android.widget.Toast
import android.graphics.Bitmap





class MainActivity : AppCompatActivity() {
    companion object{
        private const val CAMERA_RESULT = 1
        private const val GALLERY_RESULT = 2
        private const val MY_CAMERA_PERMISSION_CODE = 100
        private const val MY_GALLERY_PERMISSION_CODE = 200
    }

    private lateinit var detector: Detector
    private lateinit var bitmap: Bitmap

    private var image: ImageView? = null

    private var height = 350
    private var width = 350
    private var threshold = 350

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        detector = Detector(this)

        val camera = findViewById<ImageButton>(R.id.cameraButton)
        val gallery = findViewById<ImageButton>(R.id.galleryButton)
        image = findViewById<ImageView>(R.id.imageView)
        val detect = findViewById<Button>(R.id.detectButton)
        val results = findViewById<TextView>(R.id.resultsTextView)

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.burger)
        bitmap = getScaledDownBitmap(bitmap, threshold, true)!!
        image!!.setImageBitmap(bitmap)

        camera.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE)

            }
            else{
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, MainActivity.CAMERA_RESULT)
            }

        }

        gallery.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_GALLERY_PERMISSION_CODE)
            }
            else{
                val galleryIntent = Intent(Intent.ACTION_PICK)
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, MainActivity.GALLERY_RESULT)
            }

        }

        detect.setOnClickListener {
            val result = detector.recognizeImage(bitmap)
            results.text = ""
            for (i in result){
                results.text = (results.text as String).plus(i.toString().plus("\n"))
            }

        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        height = image!!.height
        width = image!!.width
        if(height >= width){
            threshold = height
        }
        else{
            threshold = width
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show()
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, MainActivity.CAMERA_RESULT)
                }
                else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show()
                }
                return
            }

            MY_GALLERY_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Gallery Permission Granted", Toast.LENGTH_LONG).show()
                    val galleryIntent = Intent(Intent.ACTION_PICK)
                    galleryIntent.type = "image/*"
                    startActivityForResult(galleryIntent, MainActivity.GALLERY_RESULT)
                }
                else {
                    Toast.makeText(this, "Gallery Permission Denied", Toast.LENGTH_LONG).show()
                }
            }

            else -> {

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val results = findViewById<TextView>(R.id.resultsTextView)

        if(requestCode == CAMERA_RESULT){
            if(resultCode == Activity.RESULT_OK && data !== null) {
                results.text = ""
                bitmap = data.extras!!.get("data") as Bitmap
                bitmap = getScaledDownBitmap(bitmap, threshold, true)!!
                image!!.setImageBitmap(bitmap)
            }
        }
        else if(requestCode == GALLERY_RESULT && data != null){
            results.text = ""
            val uri = data.data

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                bitmap = getScaledDownBitmap(bitmap, height, true)!!
                image!!.setImageBitmap(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }


        }
    }

    private fun getScaledDownBitmap(
        bitmap: Bitmap,
        threshold: Int,
        isNecessaryToKeepOrig: Boolean,
    ): Bitmap? {
        val width = bitmap.width
        val height = bitmap.height
        var newWidth = width
        var newHeight = height
        if (width > height && width > threshold) {
            newWidth = threshold
            newHeight = (height * newWidth.toFloat() / width).toInt()
        }
        if (width in (height + 1)..threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap
        }
        if (width < height && height > threshold) {
            newHeight = threshold
            newWidth = (width * newHeight.toFloat() / height).toInt()
        }
        if (height in (width + 1)..threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap
        }
        if (width == height && width > threshold) {
            newWidth = threshold
            newHeight = newWidth
        }
        return if (width == height && width <= threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            bitmap
        } else getResizedBitmap(bitmap, newWidth, newHeight, isNecessaryToKeepOrig)
    }

    private fun getResizedBitmap(
        bm: Bitmap,
        newWidth: Int,
        newHeight: Int,
        isNecessaryToKeepOrig: Boolean,
    ): Bitmap? {
        val widthBitmap = bm.width
        val heightBitmap = bm.height
        val scaleWidth = newWidth.toFloat() / widthBitmap
        val scaleHeight = newHeight.toFloat() / heightBitmap
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, widthBitmap, heightBitmap, matrix, true)
        if (!isNecessaryToKeepOrig) {
            bm.recycle()
        }
        return resizedBitmap
    }
}