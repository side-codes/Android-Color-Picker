package codes.side.andcolorpicker.model

class IntegerRGBColor : IntegerColor(COMPONENTS_COUNT) {
  companion object {
    private const val TAG = "IntegerRGBColor"
    private const val COMPONENTS_COUNT = 3
  }

  override val colorKey = ColorKey.RGB

  override val alpha: Float
    get() = TODO("Not yet implemented")

  override fun clone(): IntegerRGBColor {
    return IntegerRGBColor().also {
      it.setFrom(this)
    }
  }
}
