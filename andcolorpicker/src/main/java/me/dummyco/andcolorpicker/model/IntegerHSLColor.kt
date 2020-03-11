package me.dummyco.andcolorpicker.model

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt
import kotlin.random.Random

// TODO: Invent hierarchy
// TODO: Provide precision options
class IntegerHSLColor {
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

    fun createRandomColor(pure: Boolean = false): IntegerHSLColor {
      return IntegerHSLColor().setFromHSL(
        floatArrayOf(
          Random.Default.nextFloat() * 360f,
          if (pure) DEFAULT_S else Random.Default.nextFloat(),
          if (pure) DEFAULT_L else Random.Default.nextFloat()
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
      return intValues[H_INDEX]
    }
    set(value) {
      intValues[H_INDEX] = value.coerceIn(
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
      return intValues[S_INDEX]
    }
    set(value) {
      intValues[S_INDEX] = value.coerceIn(
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
      return intValues[L_INDEX]
    }
    set(value) {
      intValues[L_INDEX] = value.coerceIn(
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
  val rInt: Int
    get() {
      return Color.red(colorInt)
    }
  val gInt: Int
    get() {
      return Color.green(colorInt)
    }
  val bInt: Int
    get() {
      return Color.blue(colorInt)
    }

  private val hsColorIntHSLCache = FloatArray(3)
  val hsColorInt: Int
    get() {
      hsColorIntHSLCache[H_INDEX] = intValues[H_INDEX].toFloat()
      hsColorIntHSLCache[S_INDEX] = intValues[S_INDEX] / 100f
      hsColorIntHSLCache[L_INDEX] = DEFAULT_L
      return ColorUtils.HSLToColor(hsColorIntHSLCache)
    }
  private val colorIntHSLCache = FloatArray(3)
  val colorInt: Int
    get() {
      colorIntHSLCache[H_INDEX] = intValues[H_INDEX].toFloat()
      colorIntHSLCache[S_INDEX] = intValues[S_INDEX] / 100f
      colorIntHSLCache[L_INDEX] = intValues[L_INDEX] / 100f
      return ColorUtils.HSLToColor(colorIntHSLCache)
    }
  private val pureColorIntHSLCache = FloatArray(3)
  val pureColorInt: Int
    get() {
      pureColorIntHSLCache[H_INDEX] = intH.toFloat()
      pureColorIntHSLCache[S_INDEX] = DEFAULT_S
      pureColorIntHSLCache[L_INDEX] = DEFAULT_L
      return ColorUtils.HSLToColor(pureColorIntHSLCache)
    }

  private val intValues = IntArray(3)

  fun setFromHSL(h: Float, s: Float, l: Float): IntegerHSLColor {
    this.intH = h.roundToInt()
    this.intS = (s * 100f).roundToInt()
    this.intL = (l * 100f).roundToInt()
    return this
  }

  fun setFromHSL(hsl: FloatArray): IntegerHSLColor {
    return setFromHSL(
      hsl[H_INDEX],
      hsl[S_INDEX],
      hsl[L_INDEX]
    )
  }

  fun setFromColor(@ColorInt color: Int): IntegerHSLColor {
    val hslOutput = FloatArray(3)
    ColorUtils.colorToHSL(
      color,
      hslOutput
    )
    return setFromHSL(hslOutput)
  }

  // TODO: Cache output?
  fun setFromRGB(r: Int, g: Int, b: Int): IntegerHSLColor {
    val output = FloatArray(3)
    ColorUtils.RGBToHSL(
      r,
      g,
      b,
      output
    )
    return setFromHSL(
      output
    )
  }

  fun setFromHSLColor(hslColor: IntegerHSLColor): IntegerHSLColor {
    hslColor.copyValuesTo(intValues)
    return this
  }

  // FIXME: Unsafe, provide checks
  fun copyValuesFrom(inValues: IntArray): IntegerHSLColor {
    inValues.copyInto(intValues)
    return this
  }

  fun copyValuesTo(outValues: IntArray) {
    intValues.copyInto(outValues)
  }

  fun copy(): IntegerHSLColor {
    return IntegerHSLColor().setFromHSLColor(this)
  }

  override fun toString(): String {
    return "HSLColor(a=$intA, values=${intValues.contentToString()})"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as IntegerHSLColor

    if (_a != other._a) return false
    if (!intValues.contentEquals(other.intValues)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = _a.hashCode()
    result = 31 * result + intValues.contentHashCode()
    return result
  }
}
