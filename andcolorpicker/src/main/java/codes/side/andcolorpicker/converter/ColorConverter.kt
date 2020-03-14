package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.model.Color

interface ColorConverter<C : Color<C>> {
  @ColorInt
  fun convertToColorInt(color: C): Int
}
