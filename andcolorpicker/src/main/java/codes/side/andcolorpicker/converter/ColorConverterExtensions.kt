package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.model.Color

@ColorInt
fun Color.convertToColorInt(): Int {
  return ColorConverterHub.getConverterByKey(colorKey).convertToColorInt(this)
}

fun Color.getRInt(): Int {
  return android.graphics.Color.red(convertToColorInt())
}

fun Color.getGInt(): Int {
  return android.graphics.Color.green(convertToColorInt())
}

fun Color.getBInt(): Int {
  return android.graphics.Color.blue(convertToColorInt())
}

@ColorInt
fun Color.getContrastColor(): Int {
  return if (getRInt() * 0.299f + getGInt() * 0.587f + getBInt() * 0.114f > 186) {
    android.graphics.Color.BLACK
  } else {
    android.graphics.Color.WHITE
  }
}
