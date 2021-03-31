package codes.side.andcolorpicker.model

class IntegerRGBColor : IntegerColor(
  COMPONENTS_COUNT,
  DEFAULT_RGB_VALUES
) {

  override val colorKey = ColorKey.RGB

  override val alpha: Float
    get() {
      return intA / Component.A.maxValue.toFloat()
    }
  var intA: Int
    get() {
      return intValues[Component.A.index]
    }
    set(value) {
      setValue(
        Component.A.index,
        value,
        Component.A.minValue,
        Component.A.maxValue
      )
    }

  var floatR: Float
    get() {
      return intR.toFloat()
    }
    set(value) {
      intR = value.toInt()
    }
  var intR: Int
    get() {
      return intValues[Component.R.index]
    }
    set(value) {
      setValue(
        Component.R.index,
        value,
        Component.R.minValue,
        Component.R.maxValue
      )
    }

  var floatG: Float
    get() {
      return intG.toFloat()
    }
    set(value) {
      intG = value.toInt()
    }
  var intG: Int
    get() {
      return intValues[Component.G.index]
    }
    set(value) {
      setValue(
        Component.G.index,
        value,
        Component.G.minValue,
        Component.G.maxValue
      )
    }

  var floatB: Float
    get() {
      return intB.toFloat()
    }
    set(value) {
      intB = value.toInt()
    }
  var intB: Int
    get() {
      return intValues[Component.B.index]
    }
    set(value) {
      setValue(
        Component.B.index,
        value,
        Component.B.minValue,
        Component.B.maxValue
      )
    }

  override fun clone(): IntegerRGBColor {
    return super.clone() as IntegerRGBColor
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (!super.equals(other)) return false

    other as IntegerRGBColor

    if (colorKey != other.colorKey) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + colorKey.hashCode()
    return result
  }

  // TODO: Make Component top-level?
  // TODO: Make tree?
  // TODO: Use range?
  enum class Component(
    val defaultValue: Int,
    val minValue: Int,
    val maxValue: Int
  ) {
    R(
      0,
      0,
      255
    ),
    G(
      0,
      0,
      255
    ),
    B(
      0,
      0,
      255
    ),
    A(
      255,
      0,
      255
    );

    // TODO: Review approach
    val index: Int
      get() {
        return ordinal
      }

    // TODO: Adapt for non-zero min values
    val normalizedDefaultValue: Float
      get() {
        return defaultValue / maxValue.toFloat()
      }
  }

  companion object {
    private const val TAG = "IntegerRGBColor"
    private val COMPONENTS_COUNT = Component.values().size

    private val DEFAULT_RGB_VALUES = Component
      .values().map { it.defaultValue }.toIntArray()
  }
}
