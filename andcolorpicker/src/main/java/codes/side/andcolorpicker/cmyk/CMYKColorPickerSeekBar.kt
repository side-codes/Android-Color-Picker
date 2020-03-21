package codes.side.andcolorpicker.cmyk

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.IntegerCMYKColorConverter
import codes.side.andcolorpicker.model.IntegerCMYKColor
import codes.side.andcolorpicker.model.factory.CMYKColorFactory
import codes.side.andcolorpicker.view.picker.GradientColorSeekBar

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

    // TODO: Make configurable
    private const val COERCE_AT_LEAST_COMPONENT = 15
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

  // TODO: Attrs
  private fun init(attrs: AttributeSet? = null) {
    val typedArray = context.theme.obtainStyledAttributes(
      attrs,
      R.styleable.CMYKColorPickerSeekBar,
      0,
      0
    )

    mode = Mode.values()[typedArray.getInteger(
      R.styleable.CMYKColorPickerSeekBar_cmykMode,
      DEFAULT_MODE.ordinal
    )]
    coloringMode = ColoringMode.values()[typedArray.getInteger(
      R.styleable.CMYKColorPickerSeekBar_cmykColoringMode,
      DEFAULT_COLORING_MODE.ordinal
    )]

    typedArray.recycle()
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
    internalPickedColor.setFrom(value)
  }

  override fun refreshProperties() {
    super.refreshProperties()
    if (!modeInitialized) {
      return
    }
    max = mode.maxProgress
  }

  override fun refreshProgressDrawable() {
    super.refreshProgressDrawable()

    if (!coloringModeInitialized || !modeInitialized) {
      return
    }

    ((progressDrawable as LayerDrawable).getDrawable(0) as GradientDrawable).colors = when (mode) {
      Mode.MODE_C -> {
        when (coloringMode) {
          ColoringMode.PURE_COLOR -> Mode.MODE_C.checkpoints
          ColoringMode.OUTPUT_COLOR -> TODO()
        }
      }
      Mode.MODE_M -> {
        when (coloringMode) {
          ColoringMode.PURE_COLOR -> Mode.MODE_M.checkpoints
          ColoringMode.OUTPUT_COLOR -> TODO()
        }
      }
      Mode.MODE_Y -> {
        when (coloringMode) {
          ColoringMode.PURE_COLOR -> Mode.MODE_Y.checkpoints
          ColoringMode.OUTPUT_COLOR -> TODO()
        }
      }
      Mode.MODE_K -> {
        when (coloringMode) {
          ColoringMode.PURE_COLOR -> Mode.MODE_K.checkpoints
          ColoringMode.OUTPUT_COLOR -> TODO()
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
      Mode.MODE_C -> {
        val currentH = internalPickedColor.intC
        if (currentH != currentProgress) {
          internalPickedColor.intC = currentProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_M -> {
        val currentS = internalPickedColor.intM
        if (currentS != currentProgress) {
          internalPickedColor.intM = currentProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_Y -> {
        val currentL = internalPickedColor.intY
        if (currentL != currentProgress) {
          internalPickedColor.intY = currentProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_K -> {
        val currentL = internalPickedColor.intK
        if (currentL != currentProgress) {
          internalPickedColor.intK = currentProgress
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
      Mode.MODE_C -> {
        internalPickedColor.intC
      }
      Mode.MODE_M -> {
        internalPickedColor.intM
      }
      Mode.MODE_Y -> {
        internalPickedColor.intY
      }
      Mode.MODE_K -> {
        internalPickedColor.intK
      }
    }
  }

  // TODO: Refactor
  private fun paintThumbStroke(drawable: GradientDrawable) {
    if (!coloringModeInitialized || !modeInitialized) {
      return
    }

    val currentProgress = progress
    drawable.setStroke(
      thumbStrokeWidthPx,
      when (mode) {
        Mode.MODE_C -> {
          when (coloringMode) {
            ColoringMode.PURE_COLOR -> ColorUtils.blendARGB(
              Color.WHITE,
              Color.CYAN,
              currentProgress.coerceAtLeast(COERCE_AT_LEAST_COMPONENT) / mode.maxProgress.toFloat()
            )
            ColoringMode.OUTPUT_COLOR -> TODO()
          }
        }
        Mode.MODE_M -> {
          when (coloringMode) {
            ColoringMode.PURE_COLOR -> ColorUtils.blendARGB(
              Color.WHITE,
              Color.MAGENTA,
              currentProgress.coerceAtLeast(COERCE_AT_LEAST_COMPONENT) / mode.maxProgress.toFloat()
            )
            ColoringMode.OUTPUT_COLOR -> TODO()
          }
        }
        Mode.MODE_Y -> {
          when (coloringMode) {
            ColoringMode.PURE_COLOR -> ColorUtils.blendARGB(
              Color.WHITE,
              Color.YELLOW,
              currentProgress.coerceAtLeast(COERCE_AT_LEAST_COMPONENT) / mode.maxProgress.toFloat()
            )
            ColoringMode.OUTPUT_COLOR -> TODO()
          }
        }
        Mode.MODE_K -> {
          when (coloringMode) {
            ColoringMode.PURE_COLOR -> ColorUtils.blendARGB(
              Color.WHITE,
              Color.BLACK,
              currentProgress.coerceAtLeast(COERCE_AT_LEAST_COMPONENT) / mode.maxProgress.toFloat()
            )
            ColoringMode.OUTPUT_COLOR -> TODO()
          }
        }
      }
    )
  }

  enum class ColoringMode {
    PURE_COLOR,
    OUTPUT_COLOR
  }

  // TODO: Link mode with value
  enum class Mode(
    val minProgress: Int,
    val maxProgress: Int,
    val checkpoints: IntArray
  ) {
    MODE_C(
      IntegerCMYKColor.Component.C.minValue,
      IntegerCMYKColor.Component.C.maxValue,
      intArrayOf(
        Color.WHITE,
        Color.CYAN
      )
    ),
    MODE_M(
      IntegerCMYKColor.Component.M.minValue,
      IntegerCMYKColor.Component.M.maxValue,
      intArrayOf(
        Color.WHITE,
        Color.MAGENTA
      )
    ),
    MODE_Y(
      IntegerCMYKColor.Component.Y.minValue,
      IntegerCMYKColor.Component.Y.maxValue,
      intArrayOf(
        Color.WHITE,
        Color.YELLOW
      )
    ),
    MODE_K(
      IntegerCMYKColor.Component.K.minValue,
      IntegerCMYKColor.Component.K.maxValue,
      intArrayOf(
        Color.WHITE,
        Color.BLACK
      )
    ),
  }
}
