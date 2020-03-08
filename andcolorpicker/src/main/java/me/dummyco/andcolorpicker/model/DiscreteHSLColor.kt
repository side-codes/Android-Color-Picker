package me.dummyco.andcolorpicker.model

import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt
import kotlin.random.Random

// TODO: Invent hierarchy
// TODO: Provide precision options
class DiscreteHSLColor {
  companion object {
    const val H_INDEX = 0
    const val S_INDEX = 1
    const val L_INDEX = 2
    const val DEFAULT_H = 0f
    const val DEFAULT_S = 1f
    const val DEFAULT_L = 0.5f

    private val DEFAULT_HSL_VALUES = floatArrayOf(
      DEFAULT_H,
      DEFAULT_S,
      DEFAULT_L
    )

    fun getDefaultHSLValueByIndex(index: Int): Float {
      return DEFAULT_HSL_VALUES[index]
    }

    fun createRandomColor(): DiscreteHSLColor {
      return DiscreteHSLColor().setFromHSL(
        floatArrayOf(
          Random.Default.nextFloat() * 360f,
          Random.Default.nextFloat(),
          Random.Default.nextFloat()
        )
      )
    }
  }

  var floatH: Float
    get() {
      return intH.toFloat()
    }
    set(value) {
      intS = value.toInt()
    }
  var intH: Int
    get() {
      return values[H_INDEX]
    }
    set(value) {
      values[H_INDEX] = value.coerceIn(
        0,
        360
      )
    }
  var floatS: Float
    get() {
      return intS / 100f
    }
    set(value) {
      intS = (value * 100f).toInt()
    }
  var intS: Int
    get() {
      return values[S_INDEX]
    }
    set(value) {
      values[S_INDEX] = value.coerceIn(
        0,
        100
      )
    }
  var floatL: Float
    get() {
      return intL / 100f
    }
    set(value) {
      intL = (value * 100f).toInt()
    }
  var intL: Int
    get() {
      return values[L_INDEX]
    }
    set(value) {
      values[L_INDEX] = value.coerceIn(
        0,
        100
      )
    }
  private var _a: Int = 0
  var intA: Int
    get() {
      return _a
    }
    set(value) {
      _a = value.coerceIn(
        0,
        100
      )
    }

  private val colorIntHSLCache = floatArrayOf(
    0f,
    0f,
    0f
  )
  val colorInt: Int
    get() {
      colorIntHSLCache[H_INDEX] = values[H_INDEX].toFloat()
      colorIntHSLCache[S_INDEX] = values[S_INDEX] / 100f
      colorIntHSLCache[L_INDEX] = values[L_INDEX] / 100f
      return ColorUtils.HSLToColor(colorIntHSLCache)
    }
  private val pureColorIntHSLCache = floatArrayOf(
    0f,
    0f,
    0f
  )
  val pureColorInt: Int
    get() {
      pureColorIntHSLCache[H_INDEX] = intH.toFloat()
      pureColorIntHSLCache[S_INDEX] = DEFAULT_S
      pureColorIntHSLCache[L_INDEX] = DEFAULT_L
      return ColorUtils.HSLToColor(pureColorIntHSLCache)
    }

  private val values = intArrayOf(
    0,
    0,
    0
  )

  fun setFromHSL(h: Float, s: Float, l: Float): DiscreteHSLColor {
    this.intH = h.roundToInt()
    this.intS = (s * 100f).roundToInt()
    this.intL = (l * 100f).roundToInt()
    return this
  }

  fun setFromHSL(hsl: FloatArray): DiscreteHSLColor {
    return setFromHSL(
      hsl[H_INDEX],
      hsl[S_INDEX],
      hsl[L_INDEX]
    )
  }

  // TODO: Cache output?
  fun setFromRGB(r: Int, g: Int, b: Int): DiscreteHSLColor {
    val output = floatArrayOf(
      0f,
      0f,
      0f
    )
    ColorUtils.RGBToHSL(
      r,
      g,
      b,
      output
    )
    setFromHSL(
      output
    )
    return this
  }

  fun setFromHSLColor(hslColor: DiscreteHSLColor): DiscreteHSLColor {
    hslColor.copyValuesTo(values)
    return this
  }

  // FIXME: Unsafe, provide checks
  fun copyValuesFrom(inValues: IntArray): DiscreteHSLColor {
    inValues.copyInto(values)
    return this
  }

  fun copyValuesTo(outValues: IntArray) {
    values.copyInto(outValues)
  }

  fun copy(): DiscreteHSLColor {
    return DiscreteHSLColor().setFromHSLColor(this)
  }

  override fun toString(): String {
    return "HSLColor(a=$intA, values=${values.contentToString()})"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as DiscreteHSLColor

    if (_a != other._a) return false
    if (!values.contentEquals(other.values)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = _a.hashCode()
    result = 31 * result + values.contentHashCode()
    return result
  }
}
