package codes.side.andcolorpicker.cmyk

import android.content.Context
import android.util.AttributeSet
import codes.side.andcolorpicker.converter.IntegerCMYKColorConverter
import codes.side.andcolorpicker.model.IntegerCMYKColor
import codes.side.andcolorpicker.model.factory.CMYKColorFactory
import codes.side.andcolorpicker.view.GradientColorSeekBar

class CMYKColorPickerSeekBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = androidx.appcompat.R.attr.seekBarStyle
) :
  GradientColorSeekBar<IntegerCMYKColor>(
    CMYKColorFactory(),
    context,
    attrs,
    defStyle
  ) {
  companion object {
    private const val TAG = "CMYKColorPickerSeekBar"

    private val DEFAULT_MODE = Mode.MODE_C
    private val DEFAULT_COLORING_MODE = ColoringMode.PURE_COLOR
  }

  override val colorConverter: IntegerCMYKColorConverter
    get() = super.colorConverter as IntegerCMYKColorConverter

  private var modeInitialized = false
  private var _mode: Mode? = null
  var mode: Mode
    get() {
      return requireNotNull(_mode) { "Mode is not initialized yet" }
    }
    set(value) {
      modeInitialized = true
      if (_mode == value) {
        return
      }
      _mode = value
      refreshProperties()
      refreshProgressFromCurrentColor()
      refreshProgressDrawable()
      refreshThumb()
    }

  private var coloringModeInitialized = false
  private var _coloringMode: ColoringMode? = null
  var coloringMode: ColoringMode
    get() {
      return requireNotNull(_coloringMode) { "Coloring mode is not initialized yet" }
    }
    set(value) {
      coloringModeInitialized = true
      if (_coloringMode == value) {
        return
      }
      _coloringMode = value
      refreshProgressDrawable()
      refreshThumb()
    }

  init {
    init(attrs)
  }

  private fun init(attrs: AttributeSet? = null) {
    mode = DEFAULT_MODE
    coloringMode = DEFAULT_COLORING_MODE
  }

  override fun setMin(min: Int) {
    if (min != 0) {
      throw IllegalArgumentException("Current mode supports 0 min value only")
    }
    super.setMin(min)
  }

  override fun setMax(max: Int) {
    if (max != 100) {
      throw IllegalArgumentException("Current mode supports 100 max value only")
    }
    super.setMax(max)
  }

  override fun updateInternalPickedColorFrom(value: IntegerCMYKColor) {
    super.updateInternalPickedColorFrom(value)
    TODO()
    //internalPickedColor.setFromCMYKColor(value)
  }

  enum class ColoringMode {
    PURE_COLOR,
    OUTPUT_COLOR
  }

  enum class Mode {
    MODE_C,
    MODE_M,
    MODE_Y,
    MODE_K,
  }
}
