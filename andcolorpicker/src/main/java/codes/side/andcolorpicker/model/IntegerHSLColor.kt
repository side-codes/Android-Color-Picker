package codes.side.andcolorpicker.model

import kotlin.random.Random

// TODO: Provide precision options
// TODO: Remove float properties? Move to converters?
class IntegerHSLColor : IntegerColor(
  COMPONENTS_COUNT,
  DEFAULT_VALUES
) {
  companion object {
    private const val TAG = "IntegerHSLColor"
    private val COMPONENTS_COUNT = Component.values().size

    private val DEFAULT_VALUES = Component
      .values().map { it.defaultValue }.toIntArray()

    fun createRandomColor(pure: Boolean = false): IntegerHSLColor {
      return IntegerHSLColor().also {
        it.copyValuesFrom(
          intArrayOf(
            Random.Default.nextInt(
              Component.H.minValue,
              Component.H.maxValue
            ),
            if (pure) Component.S.defaultValue else Random.Default.nextInt(
              Component.S.minValue,
              Component.S.maxValue
            ),
            if (pure) Component.L.defaultValue else Random.Default.nextInt(
              Component.L.minValue,
              Component.L.maxValue
            )
          )
        )
      }
    }
  }

  override val colorKey = ColorKey.HSL

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

  var floatH: Float
    get() {
      return intH.toFloat()
    }
    set(value) {
      intH = value.toInt()
    }
  var intH: Int
    get() {
      return intValues[Component.H.index]
    }
    set(value) {
      setValue(
        Component.H.index,
        value,
        Component.H.minValue,
        Component.H.maxValue
      )
    }
  var floatS: Float
    get() {
      return intS / Component.S.maxValue.toFloat()
    }
    set(value) {
      intS = (value * Component.S.maxValue).toInt()
    }
  var intS: Int
    get() {
      return intValues[Component.S.index]
    }
    set(value) {
      setValue(
        Component.S.index,
        value,
        Component.S.minValue,
        Component.S.maxValue
      )
    }
  var floatL: Float
    get() {
      return intL / Component.L.maxValue.toFloat()
    }
    set(value) {
      intL = (value * Component.L.maxValue).toInt()
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

  override fun clone(): IntegerHSLColor {
    return IntegerHSLColor().also {
      it.setFrom(this)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    if (!super.equals(other)) return false

    other as IntegerHSLColor

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
    H(
      0,
      0,
      360
    ),
    S(
      100,
      0,
      100
    ),
    L(
      50,
      0,
      100
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
}
