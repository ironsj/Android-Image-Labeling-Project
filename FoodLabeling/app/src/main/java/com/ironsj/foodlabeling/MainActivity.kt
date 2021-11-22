package com.ironsj.foodlabeling

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import java.io.IOException


class MainActivity : AppCompatActivity() {
    companion object{
        const val CAMERA_RESULT = 1
        const val GALLERY_RESULT = 2
    }

    private lateinit var detecter: Detecter
    private lateinit var bitmap: Bitmap

    val height = 350
    val width = 350

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        detecter = Detecter(this)

        val camera = findViewById<ImageButton>(R.id.cameraButton)
        val gallery = findViewById<ImageButton>(R.id.galleryButton)
        val image = findViewById<ImageView>(R.id.imageView)
        val detect = findViewById<Button>(R.id.detectButton)
        val results = findViewById<TextView>(R.id.resultsTextView)



        bitmap = BitmapFactory.decodeResource(resources, R.drawable.burger)
        bitmap = scaleImage(bitmap)
        image.setImageBitmap(bitmap)

        camera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, MainActivity.CAMERA_RESULT)
        }

        gallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, MainActivity.GALLERY_RESULT)
        }

        detect.setOnClickListener {
            val result = detecter.recognizeImage(bitmap)
            results.text = ""
            for (i in result){
                results.text = (results.text as String).plus(i.toString().plus("\n"))
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
                bitmap = scaleImage(bitmap)
                imageView.setImageBitmap(bitmap)
            }
        }
        else if(requestCode == GALLERY_RESULT && data != null){
            results.text = ""
            val uri = data.data

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                bitmap = scaleImage(bitmap)
                imageView.setImageBitmap(bitmap)
            }catch (e: IOException){
                e.printStackTrace()
            }

            bitmap = scaleImage(bitmap)


        }
    }

    fun scaleImage(bitmap: Bitmap?): Bitmap {
        val view = findViewById<ImageView>(R.id.imageView)

        val orignalWidth = bitmap!!.width
        val originalHeight = bitmap.height

        val scaleWidth = width.toFloat() / orignalWidth
        val scaleHeight = height.toFloat() / originalHeight

        val nh = (originalHeight * (width.toFloat() / orignalWidth)).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, nh, true)
    }
}