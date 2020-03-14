package codes.side.andcolorpicker.converter

import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.model.IntegerHSLColor

class HSLColorConverter : ColorConverter<IntegerHSLColor> {
  private val convertToColorIntIntValuesCache = IntArray(3)
  private val convertToColorIntHSLCache = FloatArray(3)

  override fun convertToColorInt(color: IntegerHSLColor): Int {
    color.copyValuesTo(convertToColorIntIntValuesCache)
    convertToColorIntHSLCache[IntegerHSLColor.H_INDEX] =
      convertToColorIntIntValuesCache[IntegerHSLColor.H_INDEX].toFloat()
    convertToColorIntHSLCache[IntegerHSLColor.S_INDEX] =
      convertToColorIntIntValuesCache[IntegerHSLColor.S_INDEX] / 100f
    convertToColorIntHSLCache[IntegerHSLColor.L_INDEX] =
      convertToColorIntIntValuesCache[IntegerHSLColor.L_INDEX] / 100f
    return ColorUtils.HSLToColor(convertToColorIntHSLCache)
  }

  private val convertToPureColorIntHSLCache = FloatArray(3)

  fun convertToPureColorInt(color: IntegerHSLColor): Int {
    convertToPureColorIntHSLCache[IntegerHSLColor.H_INDEX] = color.floatH
    convertToPureColorIntHSLCache[IntegerHSLColor.S_INDEX] = IntegerHSLColor.DEFAULT_S
    convertToPureColorIntHSLCache[IntegerHSLColor.L_INDEX] = IntegerHSLColor.DEFAULT_L
    return ColorUtils.HSLToColor(convertToPureColorIntHSLCache)
  }
}
