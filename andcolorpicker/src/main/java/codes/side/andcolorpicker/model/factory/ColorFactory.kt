package codes.side.andcolorpicker.model.factory

import codes.side.andcolorpicker.model.ColorModel

abstract class ColorFactory<C : ColorModel> {
  abstract fun create(): C

  abstract fun createColorFrom(color: C): C
}
