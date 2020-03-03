package me.dummyco.andcolorpicker.model

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt
import kotlin.random.Random

// TODO: Make integer-based and provide precision options
class HSLColor {
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

    fun createRandomColor(): HSLColor {
      return HSLColor().setFromHSL(
        floatArrayOf(
          Random.Default.nextFloat() * 360f,
          Random.Default.nextFloat(),
          Random.Default.nextFloat()
        )
      )
    }
  }

  var h: Int
    get() {
      return values[H_INDEX]
    }
    set(value) {
      values[H_INDEX] = value.coerceIn(
        0,
        360
      )
    }
  var s: Int
    get() {
      return values[S_INDEX]
    }
    set(value) {
      values[S_INDEX] = value.coerceIn(
        0,
        100
      )
    }
  var l: Int
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
  var a: Int
    get() {
      return _a
    }
    set(value) {
      _a = value.coerceIn(
        0,
        100
      )
    }

  val colorInt: Int
    get() {
      return ColorUtils.HSLToColor(
        floatArrayOf(
          values[H_INDEX].toFloat(),
          values[S_INDEX] / 100f,
          values[L_INDEX] / 100f
        )
      )
    }
  private val _clearColorIntHSLCache = floatArrayOf(
    0f,
    0f,
    0f
  )
  val clearColorInt: Int
    get() {
      _clearColorIntHSLCache[H_INDEX] = h.toFloat()
      _clearColorIntHSLCache[S_INDEX] = DEFAULT_S
      _clearColorIntHSLCache[L_INDEX] = DEFAULT_L
      return ColorUtils.HSLToColor(_clearColorIntHSLCache)
    }

  private val values = intArrayOf(
    0,
    0,
    0
  )

  fun setFromHSL(h: Float, s: Float, l: Float): HSLColor {
    this.h = h.roundToInt()
    this.s = (s * 100f).roundToInt()
    this.l = (l * 100f).roundToInt()
    return this
  }

  fun setFromHSL(hsl: FloatArray): HSLColor {
    return setFromHSL(
      hsl[H_INDEX],
      hsl[S_INDEX],
      hsl[L_INDEX]
    )
  }

  fun setFromRGB(r: Int, g: Int, b: Int): HSLColor {
    val output = floatArrayOf(
      0f,
      0f,
      0f
    )
    ColorUtils.colorToHSL(
      Color.rgb(
        r,
        g,
        b
      ),
      output
    )
    setFromHSL(
      output
    )
    return this
  }

  fun setFromHSLColor(hslColor: HSLColor): HSLColor {
    hslColor.copyValuesTo(values)
    return this
  }

  // FIXME: Unsafe, provide checks
  fun copyValuesFrom(inValues: IntArray): HSLColor {
    inValues.copyInto(values)
    return this
  }

  fun copyValuesTo(outValues: IntArray) {
    values.copyInto(outValues)
  }

  fun copy(): HSLColor {
    return HSLColor().setFromHSLColor(this)
  }

  override fun toString(): String {
    return "HSLColor(a=$a, values=${values.contentToString()})"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as HSLColor

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
