package codes.side.andcolorpicker.lab

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.IntegerLABColorConverter
import codes.side.andcolorpicker.model.IntegerLABColor
import codes.side.andcolorpicker.model.factory.LABColorFactory
import codes.side.andcolorpicker.util.mapToIntArray
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import codes.side.andcolorpicker.view.picker.GradientColorSeekBar

// TODO: Minimize resource reads
// TODO: Add logger solution
// TODO: Add call flow diagram
// TODO: Add checks and reduce calls count
// TODO: Limit used SDK properties usage
class LABColorPickerSeekBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = androidx.appcompat.R.attr.seekBarStyle
) :
  GradientColorSeekBar<IntegerLABColor>(
    LABColorFactory(),
    context,
    attrs,
    defStyle
  ) {
  companion object {
    private const val TAG = "LABColorPickerSeekBar"

    private val DEFAULT_MODE = Mode.MODE_L
    private val DEFAULT_COLORING_MODE = ColoringMode.OUTPUT_COLOR

    private const val PROGRESS_SAMPLING_PERIOD = 10
  }

  override val colorConverter: IntegerLABColorConverter
    get() = super.colorConverter as IntegerLABColorConverter

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
    val typedArray = context.theme.obtainStyledAttributes(
      attrs,
      R.styleable.LABColorPickerSeekBar,
      0,
      0
    )

    mode = Mode.values()[typedArray.getInteger(
      R.styleable.LABColorPickerSeekBar_labMode,
      DEFAULT_MODE.ordinal
    )]
    coloringMode = ColoringMode.values()[typedArray.getInteger(
      R.styleable.LABColorPickerSeekBar_labColoringMode,
      DEFAULT_COLORING_MODE.ordinal
    )]

    typedArray.recycle()
  }

  override fun setMax(max: Int) {
    if (modeInitialized && max != mode.absoluteProgress) {
      throw IllegalArgumentException("Current mode supports ${mode.absoluteProgress} max value only, was $max")
    }
    super.setMax(max)
  }

  override fun onUpdateColorFrom(color: IntegerLABColor, value: IntegerLABColor) {
    color.setFrom(value)
  }

  override fun onRefreshProperties() {
    if (!modeInitialized) {
      return
    }
    max = mode.absoluteProgress
  }

  override fun onRefreshProgressFromColor(color: IntegerLABColor): Int? {
    if (!modeInitialized) {
      return null
    }

    return -mode.minProgress + when (mode) {
      Mode.MODE_L -> {
        internalPickedColor.intL
      }
      Mode.MODE_A -> {
        internalPickedColor.intA
      }
      Mode.MODE_B -> {
        internalPickedColor.intB
      }
    }
  }

  override fun onRefreshProgressDrawable(progressDrawable: LayerDrawable) {
    if (!coloringModeInitialized || !modeInitialized) {
      return
    }

    val sampledRangeIntArray = mode.sampledRangeIntArray
    val outputIntArray = IntArray(sampledRangeIntArray.size)

    (progressDrawable.getDrawable(0) as GradientDrawable).colors = when (mode) {
      Mode.MODE_L -> {
        when (coloringMode) {
          ColoringMode.OUTPUT_COLOR -> {
            sampledRangeIntArray.mapToIntArray(outputIntArray) {
              ColorUtils.LABToColor(
                it.toDouble(),
                internalPickedColor.intA.toDouble(),
                internalPickedColor.intB.toDouble()
              )
            }
          }
        }
      }
      Mode.MODE_A -> {
        when (coloringMode) {
          ColoringMode.OUTPUT_COLOR -> {
            sampledRangeIntArray.mapToIntArray(outputIntArray) {
              ColorUtils.LABToColor(
                internalPickedColor.intL.toDouble(),
                it.toDouble(),
                internalPickedColor.intB.toDouble()
              )
            }
          }
        }
      }
      Mode.MODE_B -> {
        when (coloringMode) {
          ColoringMode.OUTPUT_COLOR -> {
            sampledRangeIntArray.mapToIntArray(outputIntArray) {
              ColorUtils.LABToColor(
                internalPickedColor.intL.toDouble(),
                internalPickedColor.intA.toDouble(),
                it.toDouble()
              )
            }
          }
        }
      }
    }
  }

  override fun onRefreshThumb(thumbColoringDrawables: Set<Drawable>) {
    thumbColoringDrawables.forEach {
      when (it) {
        is GradientDrawable -> {
          paintThumbStroke(it)
        }
        is LayerDrawable -> {
          paintThumbStroke(it.getDrawable(0) as GradientDrawable)
        }
      }
    }
  }

  override fun onRefreshColorFromProgress(color: IntegerLABColor, progress: Int): Boolean {
    if (!modeInitialized) {
      return false
    }

    val unmaskedProgress = mode.minProgress + progress
    return when (mode) {
      Mode.MODE_L -> {
        val currentValue = color.intL
        if (currentValue != unmaskedProgress) {
          color.intL = unmaskedProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_A -> {
        val currentValue = color.intA
        if (currentValue != unmaskedProgress) {
          color.intA = unmaskedProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_B -> {
        val currentValue = color.intB
        if (currentValue != unmaskedProgress) {
          color.intB = unmaskedProgress
          true
        } else {
          false
        }
      }
    }
  }

  // TODO: Deal with int arrays
  private fun paintThumbStroke(drawable: GradientDrawable) {
    if (!coloringModeInitialized || !modeInitialized) {
      return
    }

    drawable.setStroke(
      thumbStrokeWidthPx,
      when (mode) {
        Mode.MODE_L -> {
          when (coloringMode) {
            ColoringMode.OUTPUT_COLOR -> {
              colorConverter.convertToColorInt(internalPickedColor)
            }
          }
        }
        Mode.MODE_A -> {
          when (coloringMode) {
            ColoringMode.OUTPUT_COLOR -> {
              colorConverter.convertToColorInt(internalPickedColor)
            }
          }
        }
        Mode.MODE_B -> {
          when (coloringMode) {
            ColoringMode.OUTPUT_COLOR -> {
              colorConverter.convertToColorInt(internalPickedColor)
            }
          }
        }
      }
    )
  }

  override fun toString(): String {
    return "LABColorPickerSeekBar(tag = $tag, _mode=${if (modeInitialized) mode else null}, _currentColor=$internalPickedColor)"
  }

  enum class ColoringMode {
    OUTPUT_COLOR
  }

  enum class Mode(
    override val minProgress: Int,
    override val maxProgress: Int
  ) : ColorSeekBar.Mode {
    MODE_L(
      IntegerLABColor.Component.L.minValue,
      IntegerLABColor.Component.L.maxValue
    ),
    MODE_A(
      IntegerLABColor.Component.A.minValue,
      IntegerLABColor.Component.A.maxValue
    ),
    MODE_B(
      IntegerLABColor.Component.B.minValue,
      IntegerLABColor.Component.B.maxValue
    );

    val range by lazy { minProgress..maxProgress }
    val sampledRange by lazy { range.step(PROGRESS_SAMPLING_PERIOD) }
    val sampledRangeIntArray by lazy { sampledRange.map { it }.toIntArray() }
  }

  interface OnColorPickListener :
    ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerLABColor>, IntegerLABColor>

  open class DefaultOnColorPickListener : OnColorPickListener {
    override fun onColorPicking(
      picker: ColorSeekBar<IntegerLABColor>,
      color: IntegerLABColor,
      value: Int,
      fromUser: Boolean
    ) {

    }

    override fun onColorPicked(
      picker: ColorSeekBar<IntegerLABColor>,
      color: IntegerLABColor,
      value: Int,
      fromUser: Boolean
    ) {

    }

    override fun onColorChanged(
      picker: ColorSeekBar<IntegerLABColor>,
      color: IntegerLABColor,
      value: Int
    ) {

    }
  }
}
