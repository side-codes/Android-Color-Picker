package codes.side.andcolorpicker.hsl

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.HSLColorConverter
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.model.factory.HSLColorFactory
import codes.side.andcolorpicker.view.ColorSeekBar
import codes.side.andcolorpicker.view.GradientColorSeekBar

// TODO: Add logger solution
// TODO: Add call flow diagram
// TODO: Add checks and reduce calls count
// TODO: Limit used SDK properties usage
class HSLColorPickerSeekBar :
  GradientColorSeekBar<IntegerHSLColor> {
  companion object {
    private const val TAG = "AndColorPickerSeekBar"

    // TODO: Make configurable
    private const val COERCE_AT_MOST_LIGHTNING = 0.9f
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

  override val colorConverter: HSLColorConverter
    get() = super.colorConverter as HSLColorConverter

  private var modeInitialized = false
  var mode: Mode
    set(value) {
      modeInitialized = true
      if (field == value) {
        return
      }
      field = value
      refreshProperties()
      refreshProgressFromCurrentColor()
      refreshProgressDrawable()
      refreshThumb()
    }

  private var coloringModeInitialized = false
  var coloringMode: ColoringMode
    set(value) {
      coloringModeInitialized = true
      if (field == value) {
        return
      }
      field = value
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
    mode = Mode.MODE_HUE
    coloringMode = ColoringMode.PURE_COLOR
  }

  // TODO: Make use of JvmOverloads
  constructor(context: Context) : super(
    HSLColorFactory(),
    context
  ) {
    init()
  }

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : super(
    HSLColorFactory(),
    context,
    attrs
  ) {
    init(attrs)
  }

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(
    HSLColorFactory(),
    context,
    attrs,
    defStyleAttr
  ) {
    init(attrs)
  }

  private fun init(attrs: AttributeSet? = null) {
    if (attrs != null) {
      val typedArray = context.theme.obtainStyledAttributes(
        attrs,
        R.styleable.HSLColorPickerSeekBar,
        0,
        0
      )

      try {
        mode = Mode.values()[typedArray.getInteger(
          R.styleable.HSLColorPickerSeekBar_mode,
          0
        )]
        coloringMode = ColoringMode.values()[typedArray.getInteger(
          R.styleable.HSLColorPickerSeekBar_coloring,
          0
        )]
      } finally {
        typedArray.recycle()
      }
    }
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

  override fun updateInternalCurrentColorFrom(value: IntegerHSLColor) {
    super.updateInternalCurrentColorFrom(value)
    internalPickedColor.setFromHSLColor(value)
  }

  override fun refreshProperties() {
    super.refreshProperties()
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
        createHueOutputColorCheckpointsHSLCache[IntegerHSLColor.S_INDEX] =
          internalPickedColor.floatS
        createHueOutputColorCheckpointsHSLCache[IntegerHSLColor.L_INDEX] =
          internalPickedColor.floatL
        ColorUtils.HSLToColor(createHueOutputColorCheckpointsHSLCache)
      }.toIntArray()
  }

  private fun refreshZeroSaturationOutputColorHSLCache() {
    zeroSaturationOutputColorHSLCache[2] = internalPickedColor.floatL
  }

  override fun refreshProgressDrawable() {
    super.refreshProgressDrawable()

    if (!coloringModeInitialized) {
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
              it[1] = colorConverter.convertToPureColorInt(internalPickedColor)
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
            ColoringMode.PURE_COLOR -> colorConverter.convertToPureColorInt(internalPickedColor)
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
          paintDrawableStroke(it)
        }
        is LayerDrawable -> {
          paintDrawableStroke(it.getDrawable(0) as GradientDrawable)
        }
      }
    }
  }

  // Bypass color setter
  override fun refreshInternalCurrentColorFromProgress() {
    super.refreshInternalCurrentColorFromProgress()

    val currentProgress = progress
    // TODO: Use Atomic and compare/set?
    val changed: Boolean = when (mode) {
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

  private fun paintDrawableStroke(drawable: GradientDrawable) {
    if (!coloringModeInitialized || !modeInitialized) {
      return
    }

    val thumbStrokeWidthPx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_stroke_width)

    drawable.setStroke(
      thumbStrokeWidthPx,
      when (mode) {
        Mode.MODE_HUE -> {
          when (coloringMode) {
            ColoringMode.PURE_COLOR -> {
              colorConverter.convertToPureColorInt(internalPickedColor)
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
                  it.setFromHSL(
                    internalPickedColor.intH.toFloat(),
                    progress / mode.maxProgress.toFloat(),
                    IntegerHSLColor.DEFAULT_L
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
                  it.setFromHSL(
                    internalPickedColor.floatH,
                    IntegerHSLColor.DEFAULT_S,
                    internalPickedColor.floatL.coerceAtMost(COERCE_AT_MOST_LIGHTNING)
                  )
                }
              )
            }
            ColoringMode.OUTPUT_COLOR -> {
              colorConverter.convertToColorInt(
                paintDrawableStrokeLightnessHSLCache.also {
                  it.setFromHSL(
                    internalPickedColor.floatH,
                    internalPickedColor.floatS,
                    internalPickedColor.floatL.coerceAtMost(COERCE_AT_MOST_LIGHTNING)
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
    return "HSLColorPickerSeekBar(tag = $tag, _mode=$mode, _currentColor=$internalPickedColor)"
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
      0,
      360
    ),

    // S from HSV/HSL/HSI/HSB
    MODE_SATURATION(
      0,
      100
    ),

    // INTENSITY, L/I from HSL/HSI
    MODE_LIGHTNESS(
      0,
      100
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
