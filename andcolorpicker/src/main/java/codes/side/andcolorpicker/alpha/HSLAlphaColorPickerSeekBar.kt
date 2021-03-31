package codes.side.andcolorpicker.alpha

import android.content.Context
import android.util.AttributeSet
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.IntegerHSLColorConverter
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.model.factory.HSLColorFactory

// TODO: Add modes support
class HSLAlphaColorPickerSeekBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = R.attr.seekBarStyle
) :
  AlphaColorPickerSeekBar<IntegerHSLColor>(
    HSLColorFactory(),
    context,
    attrs,
    defStyle
  ) {

  private var isInitialized = false

  override val colorConverter: IntegerHSLColorConverter
    get() = super.colorConverter as IntegerHSLColorConverter

  init {
    refreshProperties()
    isInitialized = true
  }

  override fun setMax(max: Int) {
    if (isInitialized && max != IntegerHSLColor.Component.A.maxValue) {
      throw IllegalArgumentException("Current mode supports ${IntegerHSLColor.Component.A.maxValue} max value only, was $max")
    }
    super.setMax(max)
  }

  override fun onUpdateColorFrom(color: IntegerHSLColor, value: IntegerHSLColor) {
    color.setFrom(value)
  }

  override fun onRefreshProperties() {
    max = IntegerHSLColor.Component.A.maxValue
  }

  override fun onRefreshProgressFromColor(color: IntegerHSLColor): Int {
    return color.intA
  }

  override fun onRefreshColorFromProgress(color: IntegerHSLColor, progress: Int): Boolean {
    val currentA = internalPickedColor.intA
    return if (currentA != progress) {
      internalPickedColor.intA = progress
      true
    } else {
      false
    }
  }
}
