package codes.side.andcolorpicker.converter

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.IntegerHSLColor

// Should it be generic?
class IntegerHSLColorConverter : ColorConverter {

  private val convertToColorIntHSLCache = FloatArray(3)

  override fun convertToOpaqueColorInt(color: Color): Int {
    require(color is IntegerHSLColor) { "Unsupported color type supplied" }

    // TODO: Simplify
    convertToColorIntHSLCache[IntegerHSLColor.Component.H.index] = color.floatH
    convertToColorIntHSLCache[IntegerHSLColor.Component.S.index] = color.floatS
    convertToColorIntHSLCache[IntegerHSLColor.Component.L.index] = color.floatL

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

    // TODO: Simplify
    convertToPureColorIntHSLCache[IntegerHSLColor.Component.H.index] = color.floatH
    convertToPureColorIntHSLCache[IntegerHSLColor.Component.S.index] =
      IntegerHSLColor.Component.S.normalizedDefaultValue
    convertToPureColorIntHSLCache[IntegerHSLColor.Component.L.index] =
      IntegerHSLColor.Component.L.normalizedDefaultValue
    return ColorUtils.HSLToColor(convertToPureColorIntHSLCache)
  }

  override fun setFromColorInt(color: Color, value: Int) {
    require(color is IntegerHSLColor) { "Unsupported color type supplied" }

    // TODO: Cache
    val hslOutput = FloatArray(3)
    ColorUtils.colorToHSL(
      value,
      hslOutput
    )

    // TODO: Cache
    color.copyValuesFrom(
      intArrayOf(
        hslOutput[IntegerHSLColor.Component.H.index].toInt(),
        (hslOutput[IntegerHSLColor.Component.S.index] * 100f).toInt(),
        (hslOutput[IntegerHSLColor.Component.L.index] * 100f).toInt(),
        android.graphics.Color.alpha(value)
      )
    )
  }

  private val hsColorIntHSLCache = FloatArray(3)

  @ColorInt
  fun convertToDefaultLightness(color: Color): Int {
    require(color is IntegerHSLColor) { "Unsupported color type supplied" }

    hsColorIntHSLCache[IntegerHSLColor.Component.H.index] = color.floatH
    hsColorIntHSLCache[IntegerHSLColor.Component.S.index] = color.floatS
    hsColorIntHSLCache[IntegerHSLColor.Component.L.index] =
      IntegerHSLColor.Component.L.normalizedDefaultValue
    return ColorUtils.HSLToColor(hsColorIntHSLCache)
  }
}
