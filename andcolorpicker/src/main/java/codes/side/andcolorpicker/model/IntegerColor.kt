package codes.side.andcolorpicker.model

abstract class IntegerColor(componentsCount: Int) : Color {
  companion object {
    private const val TAG = "IntegerColor"
  }

  protected val intValues = IntArray(componentsCount)
}
