package com.ironsj.foodlabeling

data class Recognition(val label:String, val confidence:Float)  {
    override fun toString(): String {
        return "$label / $probabilityString"
    }

    private val probabilityString = String.format("%.1f%%", confidence * 100.0f)
}
