package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.model.Color

abstract class BoundColorConverter<C : Color<C>>(val color: C) {
  abstract val wrappedConverter: ColorConverter<C>

  @ColorInt
  abstract fun convertToColorInt(): Int
}
