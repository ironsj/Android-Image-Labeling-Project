package com.ironsj.foodlabeling

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import java.io.IOException
import com.google.android.material.internal.ViewUtils.dpToPx




class MainActivity : AppCompatActivity() {
    companion object{
        const val CAMERA_RESULT = 1
        const val GALLERY_RESULT = 2
    }

    private lateinit var bitmap: Bitmap

    val height = 350
    val width = 350

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val camera = findViewById<ImageButton>(R.id.cameraButton)
        val gallery = findViewById<ImageButton>(R.id.galleryButton)
        val image = findViewById<ImageView>(R.id.imageView)

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageView = findViewById<ImageView>(R.id.imageView)
        if(requestCode == CAMERA_RESULT){
            if(resultCode == Activity.RESULT_OK && data !== null) {
                bitmap = data.extras!!.get("data") as Bitmap
                bitmap = scaleImage(bitmap)
                imageView.setImageBitmap(bitmap)
            }
        }
        else if(requestCode == GALLERY_RESULT && data != null){
            val uri = data.data

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
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