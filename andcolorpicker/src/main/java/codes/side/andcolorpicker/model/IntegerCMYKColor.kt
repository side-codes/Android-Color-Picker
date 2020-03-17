package codes.side.andcolorpicker.model

class IntegerCMYKColor : Color {
  override val colorKey = ColorKey.CMYK

  override val alpha: Float
    get() = TODO("Not yet implemented")

  override fun setFromHSL(h: Float, s: Float, l: Float) {
    TODO("Not yet implemented")
  }

  override fun setFromHSL(hsl: FloatArray) {
    TODO("Not yet implemented")
  }

  override fun setFromColor(color: Int) {
    TODO("Not yet implemented")
  }

  override fun setFromRGB(r: Int, g: Int, b: Int) {
    TODO("Not yet implemented")
  }
}
