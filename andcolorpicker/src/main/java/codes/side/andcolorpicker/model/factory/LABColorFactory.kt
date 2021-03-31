package codes.side.andcolorpicker.model.factory

import codes.side.andcolorpicker.model.IntegerLABColor

class LABColorFactory : ColorFactory<IntegerLABColor>() {

  override fun create(): IntegerLABColor {
    return IntegerLABColor()
  }

  override fun createColorFrom(color: IntegerLABColor): IntegerLABColor {
    return color.clone()
  }
}
