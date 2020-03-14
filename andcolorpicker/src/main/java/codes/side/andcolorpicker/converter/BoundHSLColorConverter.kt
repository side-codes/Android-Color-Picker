package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.model.IntegerHSLColor

class BoundHSLColorConverter(color: IntegerHSLColor) : BoundColorConverter<IntegerHSLColor>(color) {
  override val wrappedConverter: HSLColorConverter = HSLColorConverter()

  @ColorInt
  override fun convertToColorInt(): Int {
    return wrappedConverter.convertToColorInt(color)
  }

  @ColorInt
  fun convertToPureColorInt(): Int {
    return wrappedConverter.convertToPureColorInt(color)
  }
}
