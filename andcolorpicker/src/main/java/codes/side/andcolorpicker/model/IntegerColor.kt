package codes.side.andcolorpicker.model

import android.util.Log

abstract class IntegerColor(
  componentsCount: Int,
  defaultValues: IntArray? = null
) : Color {
  companion object {
    private const val TAG = "IntegerColor"
  }

  protected val intValues = IntArray(componentsCount)

  init {
    defaultValues?.copyInto(intValues)
  }

  protected fun setValue(index: Int, value: Int, minValue: Int, maxValue: Int) {
    intValues[index] = value.coerceIn(
      minValue,
      maxValue
    )
    Log.d(
      TAG,
      "Set $this from ${intValues.contentToString()}"
    )
  }

  fun copyValuesTo(array: IntArray) {
    intValues.copyInto(array)
  }

  fun copyValuesFrom(array: IntArray) {
    if (array.size != intValues.size) {
      Log.d(
        TAG,
        "Copying values from array with different size"
      )
    }
    array.copyInto(intValues)
  }

  fun setFrom(color: IntegerColor) {
    color.copyValuesTo(intValues)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as IntegerColor

    if (!intValues.contentEquals(other.intValues)) return false

    return true
  }

  override fun hashCode(): Int {
    return intValues.contentHashCode()
  }
}
