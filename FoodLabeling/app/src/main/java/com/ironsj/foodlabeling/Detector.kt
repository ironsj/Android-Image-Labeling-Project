package com.ironsj.foodlabeling

import android.content.Context
import android.graphics.Bitmap
import com.ironsj.foodlabeling.ml.LiteModelAiyVisionClassifierFoodV11
import org.tensorflow.lite.support.image.TensorImage

class Detector(private val context: Context){

    fun recognizeImage(bitmap: Bitmap): MutableList<Recognition> {
        val items = mutableListOf<Recognition>()
        val model = LiteModelAiyVisionClassifierFoodV11.newInstance(context)
        val image = TensorImage.fromBitmap(bitmap)
        val outputs = model.process(image).probabilityAsCategoryList.apply {
            sortByDescending { it.score }
        }.take(3)

        for (output in outputs) {
            items.add(Recognition(output.label, output.score))
        }

        model.close()

        return items
    }

}