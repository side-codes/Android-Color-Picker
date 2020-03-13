package codes.side.andcolorpicker.model.factory

import codes.side.andcolorpicker.model.IntegerHSLColorModel

class HSLColorFactory : ColorFactory<IntegerHSLColorModel>() {
  override fun create(): IntegerHSLColorModel {
    return IntegerHSLColorModel()
  }

  override fun createColorFrom(color: IntegerHSLColorModel): IntegerHSLColorModel {
    return color.clone()
  }
}
