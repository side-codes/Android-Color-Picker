package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.model.Color

@ColorInt
inline fun <reified C : Color<C>> Color<C>.convertToColorInt(): Int {
  return converter.convertToColorInt(this as C)
}

inline fun <reified C : Color<C>> Color<C>.getRInt(): Int {
  return android.graphics.Color.red(convertToColorInt())
}

inline fun <reified C : Color<C>> Color<C>.getGInt(): Int {
  return android.graphics.Color.green(convertToColorInt())
}

inline fun <reified C : Color<C>> Color<C>.getBInt(): Int {
  return android.graphics.Color.blue(convertToColorInt())
}

@ColorInt
inline fun <reified C : Color<C>> Color<C>.getContrastColor(): Int {
  return if (getRInt() * 0.299f + getGInt() * 0.587f + getBInt() * 0.114f > 186) {
    android.graphics.Color.BLACK
  } else {
    android.graphics.Color.WHITE
  }
}
