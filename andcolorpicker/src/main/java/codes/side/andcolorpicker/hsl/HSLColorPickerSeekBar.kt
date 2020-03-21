package codes.side.andcolorpicker.hsl

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.IntegerHSLColorConverter
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.model.factory.HSLColorFactory
import codes.side.andcolorpicker.view.ColorSeekBar
import codes.side.andcolorpicker.view.GradientColorSeekBar

// TODO: Minimize resource reads
// TODO: Add logger solution
// TODO: Add call flow diagram
// TODO: Add checks and reduce calls count
// TODO: Limit used SDK properties usage
class HSLColorPickerSeekBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = androidx.appcompat.R.attr.seekBarStyle
) :
  GradientColorSeekBar<IntegerHSLColor>(
    HSLColorFactory(),
    context,
    attrs,
    defStyle
  ) {
  companion object {
    private const val TAG = "AndColorPickerSeekBar"

    private val DEFAULT_MODE = Mode.MODE_HUE
    private val DEFAULT_COLORING_MODE = ColoringMode.PURE_COLOR

    // TODO: Make configurable
    private const val COERCE_AT_MOST_LIGHTNING = 90
    private val HUE_COLOR_CHECKPOINTS = intArrayOf(
      Color.RED,
      Color.YELLOW,
      Color.GREEN,
      Color.CYAN,
      Color.BLUE,
      Color.MAGENTA,
      Color.RED
    )
    private val ZERO_SATURATION_COLOR_INT = Color.rgb(
      128,
      128,
      128
    )
    private val ZERO_SATURATION_COLOR_HSL = FloatArray(3).also {
      ColorUtils.colorToHSL(
        ZERO_SATURATION_COLOR_INT,
        it
      )
    }
  }

  override val colorConverter: IntegerHSLColorConverter
    get() = super.colorConverter as IntegerHSLColorConverter

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

  private val paintDrawableStrokeSaturationHSLCache by lazy { IntegerHSLColor() }
  private val paintDrawableStrokeLightnessHSLCache by lazy { IntegerHSLColor() }

  private val progressDrawableSaturationColorsCache by lazy { IntArray(2) }
  private val progressDrawableLightnessColorsCache by lazy { IntArray(3) }

  private val zeroSaturationOutputColorHSLCache by lazy { ZERO_SATURATION_COLOR_HSL.clone() }

  private val createHueOutputColorCheckpointsHSLCache by lazy {
    FloatArray(3)
  }

  init {
    init(attrs)
  }

  private fun init(attrs: AttributeSet? = null) {
    val typedArray = context.theme.obtainStyledAttributes(
      attrs,
      R.styleable.HSLColorPickerSeekBar,
      0,
      0
    )

    mode = Mode.values()[typedArray.getInteger(
      R.styleable.HSLColorPickerSeekBar_hslMode,
      DEFAULT_MODE.ordinal
    )]
    coloringMode = ColoringMode.values()[typedArray.getInteger(
      R.styleable.HSLColorPickerSeekBar_hslColoringMode,
      DEFAULT_COLORING_MODE.ordinal
    )]

    typedArray.recycle()
  }

  override fun setMin(min: Int) {
    if (modeInitialized && min != mode.minProgress) {
      throw IllegalArgumentException("Current mode supports ${mode.minProgress} min value only")
    }
    super.setMin(min)
  }

  override fun setMax(max: Int) {
    if (modeInitialized && max != mode.maxProgress) {
      throw IllegalArgumentException("Current mode supports ${mode.maxProgress} max value only")
    }
    super.setMax(max)
  }

  override fun updateInternalPickedColorFrom(value: IntegerHSLColor) {
    super.updateInternalPickedColorFrom(value)
    internalPickedColor.setFrom(value)
  }

  override fun refreshProperties() {
    super.refreshProperties()
    if (!modeInitialized) {
      return
    }
    max = mode.maxProgress
  }

  // TODO: Get rid of toIntArray allocations
  private fun createHueOutputColorCheckpoints(): IntArray {
    return HUE_COLOR_CHECKPOINTS
      .map {
        ColorUtils.colorToHSL(
          it,
          createHueOutputColorCheckpointsHSLCache
        )
        createHueOutputColorCheckpointsHSLCache[IntegerHSLColor.Component.S.index] =
          internalPickedColor.floatS
        createHueOutputColorCheckpointsHSLCache[IntegerHSLColor.Component.L.index] =
          internalPickedColor.floatL
        ColorUtils.HSLToColor(createHueOutputColorCheckpointsHSLCache)
      }.toIntArray()
  }

  private fun refreshZeroSaturationOutputColorHSLCache() {
    zeroSaturationOutputColorHSLCache[2] = internalPickedColor.floatL
  }

  override fun refreshProgressDrawable() {
    super.refreshProgressDrawable()

    if (!coloringModeInitialized || !modeInitialized) {
      return
    }

    ((progressDrawable as LayerDrawable).getDrawable(0) as GradientDrawable).colors = when (mode) {
      Mode.MODE_HUE -> {
        when (coloringMode) {
          ColoringMode.PURE_COLOR -> HUE_COLOR_CHECKPOINTS
          ColoringMode.OUTPUT_COLOR -> createHueOutputColorCheckpoints()
        }
      }
      Mode.MODE_SATURATION -> {
        when (coloringMode) {
          ColoringMode.PURE_COLOR -> {
            progressDrawableSaturationColorsCache.also {
              it[0] =
                ZERO_SATURATION_COLOR_INT
              it[1] = colorConverter.convertToPureHueColorInt(internalPickedColor)
            }
          }
          ColoringMode.OUTPUT_COLOR -> {
            refreshZeroSaturationOutputColorHSLCache()

            progressDrawableSaturationColorsCache.also {
              it[0] =
                ColorUtils.HSLToColor(zeroSaturationOutputColorHSLCache)
              it[1] = colorConverter.convertToColorInt(internalPickedColor)
            }
          }
        }
      }
      Mode.MODE_LIGHTNESS -> {
        progressDrawableLightnessColorsCache.also {
          it[0] = Color.BLACK
          it[1] = when (coloringMode) {
            ColoringMode.PURE_COLOR -> colorConverter.convertToPureHueColorInt(internalPickedColor)
            ColoringMode.OUTPUT_COLOR -> internalPickedColor.hsColorInt
          }
          it[2] = Color.WHITE
        }
      }
    }
  }

  override fun refreshThumb() {
    super.refreshThumb()

    coloringDrawables.forEach {
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

  override fun refreshInternalPickedColorFromProgress() {
    super.refreshInternalPickedColorFromProgress()

    if (!modeInitialized) {
      return
    }

    val currentProgress = progress
    // TODO: Use Atomic and compare/set?
    val changed = when (mode) {
      Mode.MODE_HUE -> {
        val currentH = internalPickedColor.intH
        if (currentH != currentProgress) {
          internalPickedColor.intH = currentProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_SATURATION -> {
        val currentS = internalPickedColor.intS
        if (currentS != currentProgress) {
          internalPickedColor.intS = currentProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_LIGHTNESS -> {
        val currentL = internalPickedColor.intL
        if (currentL != currentProgress) {
          internalPickedColor.intL = currentProgress
          true
        } else {
          false
        }
      }
    }

    if (changed) {
      notifyListenersOnColorChanged()
    }
  }

  override fun refreshProgressFromCurrentColor() {
    super.refreshProgressFromCurrentColor()

    if (!modeInitialized) {
      return
    }

    progress = when (mode) {
      Mode.MODE_HUE -> {
        internalPickedColor.intH
      }
      Mode.MODE_SATURATION -> {
        internalPickedColor.intS
      }
      Mode.MODE_LIGHTNESS -> {
        internalPickedColor.intL
      }
    }
  }

  private fun paintThumbStroke(drawable: GradientDrawable) {
    if (!coloringModeInitialized || !modeInitialized) {
      return
    }

    drawable.setStroke(
      thumbStrokeWidthPx,
      when (mode) {
        Mode.MODE_HUE -> {
          when (coloringMode) {
            ColoringMode.PURE_COLOR -> {
              colorConverter.convertToPureHueColorInt(internalPickedColor)
            }
            ColoringMode.OUTPUT_COLOR -> {
              colorConverter.convertToColorInt(internalPickedColor)
            }
          }
        }
        Mode.MODE_SATURATION -> {
          when (coloringMode) {
            ColoringMode.PURE_COLOR -> {
              colorConverter.convertToColorInt(
                paintDrawableStrokeSaturationHSLCache.also {
                  it.copyValuesFrom(
                    intArrayOf(
                      internalPickedColor.intH,
                      internalPickedColor.intS,
                      IntegerHSLColor.Component.L.defaultValue
                    )
                  )
                }
              )
            }
            ColoringMode.OUTPUT_COLOR -> {
              colorConverter.convertToColorInt(internalPickedColor)
            }
          }
        }
        Mode.MODE_LIGHTNESS -> {
          when (coloringMode) {
            ColoringMode.PURE_COLOR -> {
              colorConverter.convertToColorInt(
                paintDrawableStrokeLightnessHSLCache.also {
                  it.copyValuesFrom(
                    intArrayOf(
                      internalPickedColor.intH,
                      IntegerHSLColor.Component.S.defaultValue,
                      internalPickedColor.intL.coerceAtMost(COERCE_AT_MOST_LIGHTNING)
                    )
                  )
                }
              )
            }
            ColoringMode.OUTPUT_COLOR -> {
              colorConverter.convertToColorInt(
                paintDrawableStrokeLightnessHSLCache.also {
                  it.copyValuesFrom(
                    intArrayOf(
                      internalPickedColor.intH,
                      internalPickedColor.intS,
                      internalPickedColor.intL.coerceAtMost(COERCE_AT_MOST_LIGHTNING)
                    )
                  )
                }
              )
            }
          }
        }
      }
    )
  }

  override fun toString(): String {
    return "HSLColorPickerSeekBar(tag = $tag, _mode=${if (modeInitialized) mode else null}, _currentColor=$internalPickedColor)"
  }

  enum class ColoringMode {
    PURE_COLOR,
    OUTPUT_COLOR
  }

  enum class Mode(
    val minProgress: Int,
    val maxProgress: Int
  ) {
    // H from HSV/HSL/HSI/HSB
    MODE_HUE(
      IntegerHSLColor.Component.H.minValue,
      IntegerHSLColor.Component.H.maxValue
    ),

    // S from HSV/HSL/HSI/HSB
    MODE_SATURATION(
      IntegerHSLColor.Component.S.minValue,
      IntegerHSLColor.Component.S.maxValue
    ),

    // INTENSITY, L/I from HSL/HSI
    MODE_LIGHTNESS(
      IntegerHSLColor.Component.L.minValue,
      IntegerHSLColor.Component.L.maxValue
    )
  }

  interface OnColorPickListener :
    ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor>

  open class DefaultOnColorPickListener : OnColorPickListener {
    override fun onColorPicking(
      picker: ColorSeekBar<IntegerHSLColor>,
      color: IntegerHSLColor,
      value: Int,
      fromUser: Boolean
    ) {

    }

    override fun onColorPicked(
      picker: ColorSeekBar<IntegerHSLColor>,
      color: IntegerHSLColor,
      value: Int,
      fromUser: Boolean
    ) {

    }

    override fun onColorChanged(
      picker: ColorSeekBar<IntegerHSLColor>,
      color: IntegerHSLColor,
      value: Int
    ) {

    }
  }
}
