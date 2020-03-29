package codes.side.andcolorpicker.converter

import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.IntegerRGBColor

class IntegerRGBColorConverter : ColorConverter {

  override fun convertToOpaqueColorInt(color: Color): Int {
    TODO("Not yet implemented")
  }

  override fun convertToColorInt(color: Color): Int {
    require(color is IntegerRGBColor) { "Unsupported color type supplied" }

    return android.graphics.Color.argb(
      color.intA,
      color.intR,
      color.intG,
      color.intB
    )
  }

  override fun convertToPureHueColorInt(color: Color): Int {
    TODO("Not yet implemented")
  }

  override fun setFromColorInt(color: Color, value: Int) {
    require(color is IntegerRGBColor) { "Unsupported color type supplied" }

    color.copyValuesFrom(
      intArrayOf(
        android.graphics.Color.alpha(value),
        android.graphics.Color.red(value),
        android.graphics.Color.green(value),
        android.graphics.Color.blue(value)
      )
    )
  }
}
