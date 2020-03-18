package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.model.Color

// TODO: Provide alpha support
interface ColorConverter {
  @ColorInt
  fun convertToOpaqueColorInt(color: Color): Int

  @ColorInt
  fun convertToColorInt(color: Color): Int

  @ColorInt
  fun convertToPureHueColorInt(color: Color): Int
}
