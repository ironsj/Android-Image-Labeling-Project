package com.ironsj.foodlabeling

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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


class MainActivity : AppCompatActivity() {
    companion object{
        private const val CAMERA_RESULT = 1
        private const val GALLERY_RESULT = 2
        private const val MY_CAMERA_PERMISSION_CODE = 100
        private const val MY_GALLERY_PERMISSION_CODE = 200
    }

    private lateinit var detector: Detector
    private lateinit var bitmap: Bitmap
    private lateinit var unscaledBitmap: Bitmap

    private var image: ImageView? = null

    private var height = 350
    private var width = 350

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
        unscaledBitmap = bitmap
        bitmap = scaleImage(bitmap)
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
            val result = detector.recognizeImage(unscaledBitmap)
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show()
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, MY_CAMERA_PERMISSION_CODE)
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
        val imageView = findViewById<ImageView>(R.id.imageView)
        val results = findViewById<TextView>(R.id.resultsTextView)

        if(requestCode == CAMERA_RESULT){
            if(resultCode == Activity.RESULT_OK && data !== null) {
                results.text = ""
                bitmap = data.extras!!.get("data") as Bitmap
                unscaledBitmap = bitmap
                bitmap = scaleImage(bitmap)
                imageView.setImageBitmap(bitmap)
            }
        }
        else if(requestCode == GALLERY_RESULT && data != null){
            results.text = ""
            val uri = data.data

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                unscaledBitmap = bitmap
                bitmap = scaleImage(bitmap)
                imageView.setImageBitmap(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }

            bitmap = scaleImage(bitmap)


        }
    }

    private fun scaleImage(bitmap: Bitmap?): Bitmap {

        val originalWidth = bitmap!!.width
        val originalHeight = bitmap.height

        width.toFloat() / originalWidth
        height.toFloat() / originalHeight

        val nh = (originalHeight * (width.toFloat() / originalWidth)).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, nh, true)
    }
}