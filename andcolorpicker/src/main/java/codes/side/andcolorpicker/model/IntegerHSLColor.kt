package codes.side.andcolorpicker.model

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.converter.HSLColorConverter
import kotlin.math.roundToInt
import kotlin.random.Random

// TODO: Invent hierarchy
// TODO: Provide precision options
class IntegerHSLColor :
  Color {
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

    // TODO: Inject
    private val CONVERTER = HSLColorConverter()

    fun getDefaultHSLValueByIndex(index: Int): Float {
      return DEFAULT_HSL_VALUES[index]
    }

    fun createRandomColor(pure: Boolean = false): IntegerHSLColor {
      return IntegerHSLColor().also {
        it.setFromHSL(
          floatArrayOf(
            Random.Default.nextFloat() * 360f,
            if (pure) DEFAULT_S else Random.Default.nextFloat(),
            if (pure) DEFAULT_L else Random.Default.nextFloat()
          )
        )
      }
    }
  }

  override val colorKey = ColorKey.HSL

  override val alpha: Float
    get() {
      return intA / 100f
    }

  private val intValues = IntArray(3)

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
  private var _a: Int = 100
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

  private val hsColorIntHSLCache = FloatArray(3)
  val hsColorInt: Int
    get() {
      hsColorIntHSLCache[H_INDEX] = intValues[H_INDEX].toFloat()
      hsColorIntHSLCache[S_INDEX] = intValues[S_INDEX] / 100f
      hsColorIntHSLCache[L_INDEX] = DEFAULT_L
      return ColorUtils.HSLToColor(hsColorIntHSLCache)
    }

  override fun setFromHSL(h: Float, s: Float, l: Float) {
    this.intH = h.roundToInt()
    this.intS = (s * 100f).roundToInt()
    this.intL = (l * 100f).roundToInt()
  }

  override fun setFromHSL(hsl: FloatArray) {
    setFromHSL(
      hsl[H_INDEX],
      hsl[S_INDEX],
      hsl[L_INDEX]
    )
  }

  override fun setFromColor(@ColorInt color: Int) {
    val hslOutput = FloatArray(3)
    ColorUtils.colorToHSL(
      color,
      hslOutput
    )
    setFromHSL(hslOutput)
  }

  // TODO: Cache output?
  override fun setFromRGB(r: Int, g: Int, b: Int) {
    val output = FloatArray(3)
    ColorUtils.RGBToHSL(
      r,
      g,
      b,
      output
    )
    setFromHSL(
      output
    )
  }

  fun setFromHSLColor(hslColor: IntegerHSLColor) {
    hslColor.copyValuesTo(intValues)
  }

  // FIXME: Unsafe, provide checks
  fun copyValuesFrom(inValues: IntArray): IntegerHSLColor {
    inValues.copyInto(intValues)
    return this
  }

  fun copyValuesTo(outValues: IntArray) {
    intValues.copyInto(outValues)
  }

  override fun clone(): IntegerHSLColor {
    return IntegerHSLColor().also {
      it.setFromHSLColor(this)
    }
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
