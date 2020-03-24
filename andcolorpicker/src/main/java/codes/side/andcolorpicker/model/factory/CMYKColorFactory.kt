package codes.side.andcolorpicker.model.factory

import codes.side.andcolorpicker.model.IntegerCMYKColor

class CMYKColorFactory : ColorFactory<IntegerCMYKColor>() {
  override fun create(): IntegerCMYKColor {
    return IntegerCMYKColor()
  }

  override fun createColorFrom(color: IntegerCMYKColor): IntegerCMYKColor {
    return color.clone()
  }
}
