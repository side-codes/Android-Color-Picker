package codes.side.andcolorpicker.model.factory

import codes.side.andcolorpicker.model.IntegerHSLColor

class HSLColorFactory : ColorFactory<IntegerHSLColor>() {

  override fun create(): IntegerHSLColor {
    return IntegerHSLColor()
  }

  override fun createColorFrom(color: IntegerHSLColor): IntegerHSLColor {
    return color.clone()
  }
}
