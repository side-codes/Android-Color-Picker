package me.dummyco.andcolorpicker.app.util

import android.graphics.Color
import androidx.annotation.ColorInt
import me.dummyco.andcolorpicker.model.IntegerHSLColor

@ColorInt
fun IntegerHSLColor.createContrastColor(): Int {
  return if (rInt * 0.299f + gInt * 0.587f + bInt * 0.114f > 186) Color.BLACK else Color.WHITE
}
