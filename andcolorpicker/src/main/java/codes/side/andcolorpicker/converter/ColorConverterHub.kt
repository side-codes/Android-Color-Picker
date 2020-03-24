package codes.side.andcolorpicker.converter

import codes.side.andcolorpicker.model.ColorKey

object ColorConverterHub {
  private val map = hashMapOf<ColorKey, ColorConverter>()

  init {
    registerConverter(
      ColorKey.HSL,
      IntegerHSLColorConverter()
    )
    registerConverter(
      ColorKey.CMYK,
      IntegerCMYKColorConverter()
    )
  }

  fun getConverterByKey(key: ColorKey): ColorConverter {
    return requireNotNull(map[key])
  }

  fun registerConverter(key: ColorKey, converter: ColorConverter) {
    map[key] = converter
  }
}
