package me.dummyco.andcolorpicker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.util.AttributeSet
import android.util.StateSet
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.graphics.ColorUtils
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar.Mode.*
import kotlin.math.roundToInt

class HSLColorPickerSeekBar : AppCompatSeekBar,
  OnSeekBarChangeListener {
  companion object {
    private const val TAG = "AndColorPickerSeekBar"
    private const val COERCE_AT_MOST_LIGHTNING = 90
    private val HUE_CHECKPOINTS = intArrayOf(
      Color.RED,
      Color.YELLOW,
      Color.GREEN,
      Color.CYAN,
      Color.BLUE,
      Color.MAGENTA,
      Color.RED
    )
  }

  private val colorPickListeners = hashSetOf<OnColorPickListener>()

  private var _mode = MODE_HUE
  var mode: Mode
    get() {
      return _mode
    }
    set(value) {
      if (_mode != value) {
        _mode = value
        refreshProperties()
        refreshProgressDrawable()
        refreshThumb()
      }
    }

  private lateinit var thumbDrawableDefaultWrapper: LayerDrawable
  private lateinit var thumbDrawablePressed: GradientDrawable
  private val coloringDrawables = hashSetOf<Drawable>()

  fun addListener(listener: OnColorPickListener) {
    colorPickListeners.add(listener)
  }

  fun removeListener(listener: OnColorPickListener) {
    colorPickListeners.remove(listener)
  }

  fun clearListeners() {
    colorPickListeners.clear()
  }

  // TODO: Wrap props in enum
  private fun refreshProperties() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      min = mode.min
    }
    max = mode.max

    when (mode) {
      MODE_HUE -> {
      }
      MODE_SATURATION -> {
      }
      MODE_VALUE -> TODO()
      MODE_LIGHTNESS -> {
      }
      MODE_ALPHA -> TODO()
    }
  }

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : super(
    context,
    attrs
  ) {
    init()
  }

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(
    context,
    attrs,
    defStyleAttr
  ) {
    init()
  }

  private fun init() {
    setOnSeekBarChangeListener(this)

    splitTrack = false

    background = background.mutate()
      .also {
        if (it is RippleDrawable) {
          // TODO: Set ripple size for pre-M too
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val rippleSizePx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_ripple_radius)
            it.radius = rippleSizePx
          }
        }
      }

    setupProgressDrawable()

    val backgroundPaddingPx = resources.getDimensionPixelOffset(R.dimen.acp_seek_background_padding)
    val thumbFullSizePx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_size_full)
    val thumbDefaultSizePx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_size_default)

    val sizeD = thumbFullSizePx - thumbDefaultSizePx
    val sizeDHalf = sizeD / 2

    thumbDrawableDefaultWrapper = LayerDrawable(
      arrayOf(
        GradientDrawable().also {
          it.color = ColorStateList.valueOf(Color.WHITE)
          it.shape = GradientDrawable.OVAL
          it.setSize(
            thumbDefaultSizePx,
            thumbDefaultSizePx
          )
        }
      )
    ).also {
      it.setLayerInset(
        0,
        sizeDHalf,
        sizeDHalf,
        sizeDHalf,
        sizeDHalf
      )
    }

    thumbDrawablePressed = GradientDrawable().also {
      it.color = ColorStateList.valueOf(Color.WHITE)
      it.shape = GradientDrawable.OVAL
      it.setSize(
        thumbFullSizePx,
        thumbFullSizePx
      )
    }

    coloringDrawables.add(thumbDrawableDefaultWrapper)
    coloringDrawables.add(thumbDrawablePressed)

    thumb = AnimatedStateListDrawable().also { animatedStateListDrawable ->
      animatedStateListDrawable.addState(
        intArrayOf(android.R.attr.state_pressed),
        thumbDrawablePressed,
        1
      )
      animatedStateListDrawable.addState(
        StateSet.WILD_CARD,
        thumbDrawableDefaultWrapper,
        0
      )
      //animatedStateListDrawable.addTransition(
      //    0,
      //    1,
      //    AnimationDrawable().also {
      //      it.addFrame(
      //          GradientDrawable().also {
      //            it.setSize(
      //                160,
      //                160
      //            )
      //            it.color = ColorStateList.valueOf(Color.BLACK)
      //          },
      //          1500
      //      )
      //      it.addFrame(
      //          GradientDrawable().also {
      //            it.setSize(
      //                160,
      //                160
      //            )
      //            it.color = ColorStateList.valueOf(Color.BLUE)
      //          },
      //          1500
      //      )
      //    },
      //    true
      //)
    }

    thumbOffset -= backgroundPaddingPx / 2

    // TODO: Find good place for that
    refreshProperties()
    refreshProgressDrawable()
    refreshThumb()
  }

  private fun setupProgressDrawable() {
    val backgroundPaddingPx = resources.getDimensionPixelOffset(R.dimen.acp_seek_background_padding)

    progressDrawable = LayerDrawable(
      arrayOf(
        GradientDrawable().also {
          it.orientation = GradientDrawable.Orientation.LEFT_RIGHT
          it.cornerRadius =
            resources.getDimensionPixelOffset(R.dimen.acp_seek_background_corner_radius)
              .toFloat()
          it.shape = GradientDrawable.RECTANGLE
          // TODO: Make stroke configurable
          //it.setStroke(
          //  4,
          //  Color.rgb(
          //    192,
          //    192,
          //    192
          //  )
          //)
        }
      )
    ).also {
      it.setLayerInset(
        0,
        backgroundPaddingPx,
        backgroundPaddingPx,
        backgroundPaddingPx,
        backgroundPaddingPx
      )
    }

    refreshProgressDrawable()
  }

  private fun refreshProgressDrawable() {
    ((progressDrawable as LayerDrawable).getDrawable(0) as GradientDrawable).colors = when (mode) {
      MODE_HUE -> {
        HUE_CHECKPOINTS
      }
      MODE_SATURATION -> {
        intArrayOf(
          Color.rgb(
            128,
            128,
            128
          ),
          // TODO: Use color
          _currentColor.clearColorInt
        )
      }
      MODE_VALUE -> TODO()
      MODE_LIGHTNESS -> {
        intArrayOf(
          Color.BLACK,
          _currentColor.clearColorInt,
          Color.WHITE
        )
      }
      MODE_ALPHA -> TODO()
    }
  }

  private fun refreshThumb() {
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

  private val paintDrawableStrokeSaturationHSLCache = floatArrayOf(
    0f,
    0f,
    0f
  )
  private val paintDrawableStrokeLightnessHSLCache = floatArrayOf(
    0f,
    0f,
    0f
  )

  private fun paintDrawableStroke(drawable: GradientDrawable) {
    val thumbStrokeWidthPx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_stroke_width)

    drawable.setStroke(
      thumbStrokeWidthPx,
      when (mode) {
        MODE_HUE -> {
          _currentColor.clearColorInt
        }
        MODE_SATURATION -> {
          paintDrawableStrokeSaturationHSLCache[0] = _currentColor.h
          paintDrawableStrokeSaturationHSLCache[1] = progress / mode.max.toFloat()
          paintDrawableStrokeSaturationHSLCache[2] = 0.5f
          ColorUtils.HSLToColor(paintDrawableStrokeSaturationHSLCache)
        }
        MODE_VALUE -> TODO()
        MODE_LIGHTNESS -> {
          paintDrawableStrokeLightnessHSLCache[0] = _currentColor.h
          paintDrawableStrokeLightnessHSLCache[1] = 1f
          paintDrawableStrokeLightnessHSLCache[2] =
            progress.coerceAtMost(COERCE_AT_MOST_LIGHTNING) / mode.max.toFloat()
          ColorUtils.HSLToColor(paintDrawableStrokeLightnessHSLCache)
        }
        MODE_ALPHA -> TODO()
      }
    )
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
  }

  override fun onProgressChanged(
    seekBar: SeekBar,
    progress: Int,
    fromUser: Boolean
  ) {
    refreshCurrentColor()
    refreshThumb()
    colorPickListeners.forEach {
      it.onColorPicking(
        this,
        _currentColor,
        mode,
        progress,
        fromUser
      )
    }
  }

  private fun refreshCurrentColor() {
    when (mode) {
      MODE_HUE -> {
        _currentColor.h = progress.toFloat()
      }
      MODE_SATURATION -> {

      }
      MODE_VALUE -> TODO()
      MODE_LIGHTNESS -> {

      }
      MODE_ALPHA -> TODO()
    }
  }

  private fun refreshProgress() {
    when (mode) {
      MODE_HUE -> {
        progress = _currentColor.h.roundToInt()
      }
      MODE_SATURATION -> {

      }
      MODE_VALUE -> TODO()
      MODE_LIGHTNESS -> {

      }
      MODE_ALPHA -> TODO()
    }
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    colorPickListeners.forEach {
      it.onColorPicked(
        this,
        currentColor,
        mode,
        progress,
        true
      )
    }
  }

  // Internal holder for non-calculated modes
  private var _currentColor = HSLColor()
  // TODO: Copy on read / copy on write?
  // TODO: To method?
  var currentColor: HSLColor
    get() {
      return when (mode) {
        MODE_VALUE -> TODO()
        MODE_ALPHA -> TODO()
        else -> _currentColor.copy()
      }
    }
    set(value) {
      // TODO: Revisit whether to place it here or under branches
      _currentColor = value.copy()
      refreshProgress()
      refreshProgressDrawable()
      refreshThumb()
    }

  interface OnColorPickListener {
    fun onColorPicking(
      picker: HSLColorPickerSeekBar,
      color: HSLColor,
      mode: Mode,
      value: Int,
      fromUser: Boolean
    )

    fun onColorPicked(
      picker: HSLColorPickerSeekBar,
      color: HSLColor,
      mode: Mode,
      value: Int,
      fromUser: Boolean
    )
  }

  enum class Mode(
    val min: Int,
    val max: Int
  ) {
    MODE_HUE(
      0,
      360
    ),// H from HSV/HSL/HSI/HSB
    MODE_SATURATION(
      0,
      100
    ),// S from HSV/HSL/HSI/HSB
    MODE_VALUE(
      0,
      100
    ),// BRIGHTNESS, V/B from HSV/HSB
    // TODO: Do we need this mode?
    MODE_LIGHTNESS(
      0,
      100
    ), // INTENSITY, L/I from HSL/HSI
    MODE_ALPHA(
      0,
      360
    )
  }

  class HSLColor {
    var h: Float
      get() {
        return values[0]
      }
      set(value) {
        values[0] = value
      }
    var s: Float
      get() {
        return values[1]
      }
      set(value) {
        values[1] = value
      }
    var l: Float
      get() {
        return values[2]
      }
      set(value) {
        values[2] = value
      }
    var a: Float = 0f

    val colorInt: Int
      get() {
        return ColorUtils.HSLToColor(values)
      }
    private val _clearColorIntHSLCache = floatArrayOf(
      0f,
      0f,
      0f
    )
    val clearColorInt: Int
      get() {
        _clearColorIntHSLCache[0] = h
        _clearColorIntHSLCache[1] = 1f
        _clearColorIntHSLCache[2] = 0.5f
        return ColorUtils.HSLToColor(_clearColorIntHSLCache)
      }

    // TODO: Copy on read? Ensure read-only
    private val values = floatArrayOf(
      0f,
      0f,
      0f
    )

    fun setFromRGB(r: Int, g: Int, b: Int): HSLColor {
      ColorUtils.colorToHSL(
        Color.rgb(
          r,
          g,
          b
        ),
        values
      )
      return this
    }

    private fun setFromHSLColor(hslColor: HSLColor): HSLColor {
      hslColor.copyValuesTo(values)
      return this
    }

    private fun copyValuesTo(outValues: FloatArray) {
      values.copyInto(outValues)
    }

    fun copy(): HSLColor {
      return HSLColor().setFromHSLColor(this)
    }
  }
}

class PickerGroup : HSLColorPickerSeekBar.OnColorPickListener {
  private val pickers = hashSetOf<HSLColorPickerSeekBar>()

  fun registerPicker(picker: HSLColorPickerSeekBar) {
    picker.addListener(this)
    pickers.add(picker)
  }

  fun unregisterPicker(picker: HSLColorPickerSeekBar) {
    picker.removeListener(this)
    pickers.remove(picker)
  }

  override fun onColorPicking(
    picker: HSLColorPickerSeekBar,
    color: HSLColorPickerSeekBar.HSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int,
    fromUser: Boolean
  ) {
    notifyGroupFrom(
      picker,
      color
    )
  }

  override fun onColorPicked(
    picker: HSLColorPickerSeekBar,
    color: HSLColorPickerSeekBar.HSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int,
    fromUser: Boolean
  ) {
    notifyGroupFrom(
      picker,
      color
    )
  }

  private fun notifyGroupFrom(
    picker: HSLColorPickerSeekBar,
    color: HSLColorPickerSeekBar.HSLColor
  ) {
    pickers.filter { it != picker }.forEach {
      it.currentColor = color
    }
  }
}

fun PickerGroup.registerPickers(vararg pickers: HSLColorPickerSeekBar) {
  registerPickers(listOf(*pickers))
}

fun PickerGroup.registerPickers(pickers: Iterable<HSLColorPickerSeekBar>) {
  pickers.forEach {
    registerPicker(it)
  }
}
