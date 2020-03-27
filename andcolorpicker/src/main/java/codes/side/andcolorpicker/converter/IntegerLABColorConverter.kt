package codes.side.andcolorpicker.converter

import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.IntegerLABColor

class IntegerLABColorConverter : ColorConverter {
  override fun convertToOpaqueColorInt(color: Color): Int {
    TODO("Not yet implemented")
  }

  override fun convertToColorInt(color: Color): Int {
    require(color is IntegerLABColor) { "Unsupported color type supplied" }

    return ColorUtils.LABToColor(
      color.intL.toDouble(),
      color.intA.toDouble(),
      color.intB.toDouble()
    )
  }

  override fun convertToPureHueColorInt(color: Color): Int {
    TODO("Not yet implemented")
  }

  override fun setFromColorInt(color: Color, value: Int) {
    require(color is IntegerLABColor) { "Unsupported color type supplied" }
    TODO("Not yet implemented")
  }
}
