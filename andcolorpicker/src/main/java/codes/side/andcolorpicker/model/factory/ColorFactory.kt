package codes.side.andcolorpicker.model.factory

import codes.side.andcolorpicker.model.Color

abstract class ColorFactory<C : Color> {

  abstract fun create(): C

  abstract fun createColorFrom(color: C): C
}
