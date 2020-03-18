package codes.side.andcolorpicker.converter

import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.IntegerHSLColor

class IntegerHSLColorConverter : ColorConverter {
  private val convertToColorIntHSLCache = FloatArray(3)
  override fun convertToOpaqueColorInt(color: Color): Int {
    require(color is IntegerHSLColor) { "Unsupported color type supplied" }

    convertToColorIntHSLCache[IntegerHSLColor.H_INDEX] = color.floatH
    convertToColorIntHSLCache[IntegerHSLColor.S_INDEX] = color.floatS
    convertToColorIntHSLCache[IntegerHSLColor.L_INDEX] = color.floatL

    return ColorUtils.HSLToColor(convertToColorIntHSLCache)
  }

  override fun convertToColorInt(color: Color): Int {
    require(color is IntegerHSLColor) { "Unsupported color type supplied" }

    val opaqueColor = convertToOpaqueColorInt(color)
    return ColorUtils.setAlphaComponent(
      opaqueColor,
      color.intA
    )
  }

  private val convertToPureColorIntHSLCache = FloatArray(3)

  override fun convertToPureHueColorInt(color: Color): Int {
    require(color is IntegerHSLColor) { "Unsupported color type supplied" }

    convertToPureColorIntHSLCache[IntegerHSLColor.H_INDEX] = color.floatH
    convertToPureColorIntHSLCache[IntegerHSLColor.S_INDEX] = IntegerHSLColor.DEFAULT_S
    convertToPureColorIntHSLCache[IntegerHSLColor.L_INDEX] = IntegerHSLColor.DEFAULT_L
    return ColorUtils.HSLToColor(convertToPureColorIntHSLCache)
  }
}
