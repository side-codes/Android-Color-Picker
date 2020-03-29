package codes.side.andcolorpicker.rgb

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.IntegerRGBColorConverter
import codes.side.andcolorpicker.model.IntegerRGBColor
import codes.side.andcolorpicker.model.factory.RGBColorFactory
import codes.side.andcolorpicker.view.picker.GradientColorSeekBar

class RGBColorPickerSeekBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = androidx.appcompat.R.attr.seekBarStyle
) :
  GradientColorSeekBar<IntegerRGBColor>(
    RGBColorFactory(),
    context,
    attrs,
    defStyle
  ) {
  companion object {
    private const val TAG = "RGBColorPickerSeekBar"

    private val DEFAULT_MODE = Mode.MODE_R
    private val DEFAULT_COLORING_MODE = ColoringMode.PURE_COLOR
  }

  override val colorConverter: IntegerRGBColorConverter
    get() = super.colorConverter as IntegerRGBColorConverter

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
      R.styleable.RGBColorPickerSeekBar,
      0,
      0
    )

    mode = Mode.values()[typedArray.getInteger(
      R.styleable.RGBColorPickerSeekBar_rgbMode,
      DEFAULT_MODE.ordinal
    )]
    coloringMode = ColoringMode.values()[typedArray.getInteger(
      R.styleable.RGBColorPickerSeekBar_rgbColoringMode,
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

  override fun updateInternalPickedColorFrom(value: IntegerRGBColor) {
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

    ((progressDrawable as LayerDrawable).getDrawable(0) as GradientDrawable).colors =
      when (coloringMode) {
        ColoringMode.PURE_COLOR, ColoringMode.PLAIN_COLOR -> mode.coloringModeCheckpointsMap[coloringMode]
        ColoringMode.OUTPUT_COLOR -> when (mode) {
          Mode.MODE_R -> TODO()
          Mode.MODE_G -> TODO()
          Mode.MODE_B -> TODO()
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
      Mode.MODE_R -> {
        val currentH = internalPickedColor.intR
        if (currentH != currentProgress) {
          internalPickedColor.intR = currentProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_G -> {
        val currentS = internalPickedColor.intG
        if (currentS != currentProgress) {
          internalPickedColor.intG = currentProgress
          true
        } else {
          false
        }
      }
      Mode.MODE_B -> {
        val currentL = internalPickedColor.intB
        if (currentL != currentProgress) {
          internalPickedColor.intB = currentProgress
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
      Mode.MODE_R -> {
        internalPickedColor.intR
      }
      Mode.MODE_G -> {
        internalPickedColor.intG
      }
      Mode.MODE_B -> {
        internalPickedColor.intB
      }
    }
  }

  private fun paintThumbStroke(drawable: GradientDrawable) {
    if (!coloringModeInitialized || !modeInitialized) {
      return
    }

    val currentProgress = progress
    drawable.setStroke(
      thumbStrokeWidthPx,
      when (coloringMode) {
        ColoringMode.PURE_COLOR, ColoringMode.PLAIN_COLOR -> {
          val checkpoints = requireNotNull(mode.coloringModeCheckpointsMap[coloringMode])
          ColorUtils.blendARGB(
            checkpoints.first(),
            checkpoints.last(),
            currentProgress / mode.maxProgress.toFloat()
          )
        }
        ColoringMode.OUTPUT_COLOR -> when (mode) {
          Mode.MODE_R -> TODO()
          Mode.MODE_G -> TODO()
          Mode.MODE_B -> TODO()
        }
      }
    )
  }

  enum class ColoringMode {
    PURE_COLOR,
    OUTPUT_COLOR,
    PLAIN_COLOR,
  }

  enum class Mode(
    val minProgress: Int,
    val maxProgress: Int,
    val coloringModeCheckpointsMap: HashMap<ColoringMode, IntArray>
  ) {
    MODE_R(
      IntegerRGBColor.Component.R.minValue,
      IntegerRGBColor.Component.R.maxValue,
      hashMapOf(
        ColoringMode.PURE_COLOR to intArrayOf(
          Color.BLACK,
          Color.RED
        ),
        ColoringMode.PLAIN_COLOR to intArrayOf(
          Color.RED,
          Color.RED
        )
      )
    ),
    MODE_G(
      IntegerRGBColor.Component.G.minValue,
      IntegerRGBColor.Component.G.maxValue,
      hashMapOf(
        ColoringMode.PURE_COLOR to intArrayOf(
          Color.BLACK,
          Color.GREEN
        ),
        ColoringMode.PLAIN_COLOR to intArrayOf(
          Color.GREEN,
          Color.GREEN
        )
      )
    ),
    MODE_B(
      IntegerRGBColor.Component.B.minValue,
      IntegerRGBColor.Component.B.maxValue,
      hashMapOf(
        ColoringMode.PURE_COLOR to intArrayOf(
          Color.BLACK,
          Color.BLUE
        ),
        ColoringMode.PLAIN_COLOR to intArrayOf(
          Color.BLUE,
          Color.BLUE
        )
      )
    );
  }
}
