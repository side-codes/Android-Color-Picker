package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.model.Color

@ColorInt
fun Color.toColorInt(): Int {
  return ColorConverterHub.getConverterByKey(colorKey).convertToColorInt(this)
}

@ColorInt
fun Color.toOpaqueColorInt(): Int {
  return ColorConverterHub.getConverterByKey(colorKey).convertToOpaqueColorInt(this)
}

@ColorInt
fun Color.toPureHueColorInt(): Int {
  return ColorConverterHub.getConverterByKey(colorKey).convertToPureHueColorInt(this)
}

fun Color.getRInt(): Int {
  return android.graphics.Color.red(toColorInt())
}

fun Color.getGInt(): Int {
  return android.graphics.Color.green(toColorInt())
}

fun Color.getBInt(): Int {
  return android.graphics.Color.blue(toColorInt())
}

@ColorInt
fun Color.toContrastColor(): Int {
  return if (alpha < 0.5f || (getRInt() * 0.299f + getGInt() * 0.587f + getBInt() * 0.114f > 186)) {
    android.graphics.Color.BLACK
  } else {
    android.graphics.Color.WHITE
  }
}
