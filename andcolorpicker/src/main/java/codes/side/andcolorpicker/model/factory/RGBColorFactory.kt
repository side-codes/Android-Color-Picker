package codes.side.andcolorpicker.model.factory

import codes.side.andcolorpicker.model.IntegerRGBColor

class RGBColorFactory : ColorFactory<IntegerRGBColor>() {
  override fun create(): IntegerRGBColor {
    return IntegerRGBColor()
  }

  override fun createColorFrom(color: IntegerRGBColor): IntegerRGBColor {
    return color.clone()
  }
}
