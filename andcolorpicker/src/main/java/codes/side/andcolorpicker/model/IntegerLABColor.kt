package codes.side.andcolorpicker.model

// TODO: Provide precision options
// TODO: Remove float properties? Move to converters?
class IntegerLABColor : IntegerColor(
  COMPONENTS_COUNT,
  DEFAULT_VALUES
) {

  override val colorKey = ColorKey.LAB

  override val alpha: Float
    get() {
      return intAlpha / Component.ALPHA.maxValue.toFloat()
    }

  var intAlpha: Int
    get() {
      return intValues[Component.ALPHA.index]
    }
    set(value) {
      setValue(
        Component.ALPHA.index,
        value,
        Component.ALPHA.minValue,
        Component.ALPHA.maxValue
      )
    }

  var intL: Int
    get() {
      return intValues[Component.L.index]
    }
    set(value) {
      setValue(
        Component.L.index,
        value,
        Component.L.minValue,
        Component.L.maxValue
      )
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

  override fun clone(): IntegerLABColor {
    return IntegerLABColor().also {
      it.setFrom(this)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (!super.equals(other)) return false

    other as IntegerLABColor

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
    L(
      50,
      0,
      100
    ),
    A(
      0,
      -128,
      127
    ),
    B(
      0,
      -128,
      127
    ),
    ALPHA(
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
    private val COMPONENTS_COUNT = Component.values().size

    private val DEFAULT_VALUES = Component
      .values().map { it.defaultValue }.toIntArray()
  }
}

var IntegerLABColor.floatL: Float
  get() {
    return intL.toFloat()
  }
  set(value) {
    intL = value.toInt()
  }

var IntegerLABColor.floatA: Float
  get() {
    return intA / IntegerLABColor.Component.A.maxValue.toFloat()
  }
  set(value) {
    intA = (value * IntegerLABColor.Component.A.maxValue).toInt()
  }

var IntegerLABColor.floatB: Float
  get() {
    return intB / IntegerLABColor.Component.B.maxValue.toFloat()
  }
  set(value) {
    intB = (value * IntegerLABColor.Component.B.maxValue).toInt()
  }
