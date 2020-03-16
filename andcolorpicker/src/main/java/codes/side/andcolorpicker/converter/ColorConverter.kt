package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.model.Color

interface ColorConverter {
  @ColorInt
  fun convertToColorInt(color: Color): Int
}
