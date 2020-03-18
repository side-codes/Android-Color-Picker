package codes.side.andcolorpicker.model

class IntegerCMYKColor : IntegerColor(COMPONENTS_COUNT) {
  companion object {
    private const val TAG = "IntegerCMYKColor"
    private const val COMPONENTS_COUNT = 5
    private const val MAX_VALUE = 100
    const val C_INDEX = 0
    const val M_INDEX = 1
    const val Y_INDEX = 2
    const val K_INDEX = 3
    const val A_INDEX = 4
  }

  override val colorKey = ColorKey.CMYK

  override val alpha: Float
    get() {
      return intA / MAX_VALUE.toFloat()
    }

  var intA: Int
    get() {
      return intValues[A_INDEX]
    }
    set(value) {
      setValue(
        A_INDEX,
        value
      )
    }

  var floatC: Float
    get() {
      return intC.toFloat()
    }
    set(value) {
      intC = value.toInt()
    }
  var intC: Int
    get() {
      return intValues[C_INDEX]
    }
    set(value) {
      setValue(
        C_INDEX,
        value
      )
    }

  var floatM: Float
    get() {
      return intM / MAX_VALUE.toFloat()
    }
    set(value) {
      intM = (value * MAX_VALUE).toInt()
    }
  var intM: Int
    get() {
      return intValues[M_INDEX]
    }
    set(value) {
      setValue(
        M_INDEX,
        value
      )
    }

  var floatY: Float
    get() {
      return intY / MAX_VALUE.toFloat()
    }
    set(value) {
      intY = (value * MAX_VALUE).toInt()
    }
  var intY: Int
    get() {
      return intValues[Y_INDEX]
    }
    set(value) {
      setValue(
        Y_INDEX,
        value
      )
    }

  var floatK: Float
    get() {
      return intK / MAX_VALUE.toFloat()
    }
    set(value) {
      intK = (value * MAX_VALUE).toInt()
    }
  var intK: Int
    get() {
      return intValues[K_INDEX]
    }
    set(value) {
      setValue(
        K_INDEX,
        value
      )
    }

  private fun setValue(index: Int, value: Int) {
    intValues[index] = value.coerceIn(
      0,
      MAX_VALUE
    )
  }

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
