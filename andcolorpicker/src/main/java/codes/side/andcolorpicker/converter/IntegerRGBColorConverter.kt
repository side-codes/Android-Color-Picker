package codes.side.andcolorpicker.converter

import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.IntegerRGBColor

class IntegerRGBColorConverter : ColorConverter {

  override fun convertToOpaqueColorInt(color: Color): Int {
    TODO("Not yet implemented")
  }

  override fun convertToColorInt(color: Color): Int {
    require(color is IntegerRGBColor) { "Unsupported color type supplied" }

    return android.graphics.Color.rgb(
      color.floatR.toInt(),
      color.floatG.toInt(),
      color.floatB.toInt()
    )
  }

  override fun convertToPureHueColorInt(color: Color): Int {
    TODO("Not yet implemented")
  }

  override fun setFromColorInt(color: Color, value: Int) {
    require(color is IntegerRGBColor) { "Unsupported color type supplied" }

    color.copyValuesFrom(
      intArrayOf(
        android.graphics.Color.red(value),
        android.graphics.Color.green(value),
        android.graphics.Color.blue(value)
      )
    )
  }
}
