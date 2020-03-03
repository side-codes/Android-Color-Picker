package me.dummyco.andcolorpicker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.StateSet
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.graphics.ColorUtils
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar.Mode.*
import me.dummyco.andcolorpicker.model.HSLColor

// TODO: Add logger solution
// TODO: Add call flow diagram
// TODO: Add checks and reduce calls count
class HSLColorPickerSeekBar : AppCompatSeekBar,
  OnSeekBarChangeListener {
  companion object {
    private const val TAG = "AndColorPickerSeekBar"
    private const val DEBUG = true
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
  private var isInitialized = false

  private var _mode = MODE_HUE
  var mode: Mode
    get() {
      return _mode
    }
    set(value) {
      if (_mode != value) {
        _mode = value
        refreshProperties()
        refreshProgressFromCurrentColor()
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

  private var propertiesUpdateInProcess = false

  // TODO: Wrap props in enum
  private fun refreshProperties() {
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshProperties() called on $this"
      )
    }

    propertiesUpdateInProcess = true
    max = mode.maxProgress
    propertiesUpdateInProcess = false
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

    isInitialized = true

    // TODO: Find good place for that
    refreshProperties()
    refreshInternalCurrentColorFromProgress()
    refreshProgressDrawable()
    refreshThumb()
  }

  private fun setupProgressDrawable() {
    if (DEBUG) {
      Log.d(
        TAG,
        "setupProgressDrawable() called on $this"
      )
    }

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
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshProgressDrawable() called on $this"
      )
    }

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
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshThumb() called on $this"
      )
    }

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
          paintDrawableStrokeSaturationHSLCache[HSLColor.H_INDEX] = _currentColor.h.toFloat()
          paintDrawableStrokeSaturationHSLCache[HSLColor.S_INDEX] =
            progress / mode.maxProgress.toFloat()
          paintDrawableStrokeSaturationHSLCache[HSLColor.L_INDEX] = HSLColor.DEFAULT_L
          ColorUtils.HSLToColor(paintDrawableStrokeSaturationHSLCache)
        }
        MODE_VALUE -> TODO()
        MODE_LIGHTNESS -> {
          paintDrawableStrokeLightnessHSLCache[HSLColor.H_INDEX] = _currentColor.h.toFloat()
          paintDrawableStrokeLightnessHSLCache[HSLColor.S_INDEX] = HSLColor.DEFAULT_S
          paintDrawableStrokeLightnessHSLCache[HSLColor.L_INDEX] =
            progress.coerceAtMost(COERCE_AT_MOST_LIGHTNING) / mode.maxProgress.toFloat()
          ColorUtils.HSLToColor(paintDrawableStrokeLightnessHSLCache)
        }
        MODE_ALPHA -> TODO()
      }
    )
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
  }

  // TODO: Revisit
  override fun onProgressChanged(
    seekBar: SeekBar,
    progress: Int,
    fromUser: Boolean
  ) {
    if (propertiesUpdateInProcess) {
      return
    }

    refreshInternalCurrentColorFromProgress()
    refreshProgressDrawable()
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

  private fun notifyListenersOnColorChanged() {
    colorPickListeners.forEach {
      it.onColorChanged(
        this,
        _currentColor,
        mode,
        progress
      )
    }
  }

  // Bypass color setter
  private fun refreshInternalCurrentColorFromProgress() {
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshInternalCurrentColorFromProgress() called on $this"
      )
    }

    val currentProgress = progress
    // TODO: Use Atomic and compare/set?
    val changed: Boolean = when (mode) {
      MODE_HUE -> {
        val currentH = _currentColor.h
        if (currentH != currentProgress) {
          _currentColor.h = currentProgress
          true
        } else {
          false
        }
      }
      MODE_SATURATION -> {
        val currentS = _currentColor.s
        if (currentS != currentProgress) {
          _currentColor.s = currentProgress
          true
        } else {
          false
        }
      }
      MODE_VALUE -> TODO()
      MODE_LIGHTNESS -> {
        val currentL = _currentColor.l
        if (currentL != currentProgress) {
          _currentColor.l = currentProgress
          true
        } else {
          false
        }
      }
      MODE_ALPHA -> TODO()
    }

    if (changed) {
      notifyListenersOnColorChanged()
    }
  }

  private fun refreshProgressFromCurrentColor() {
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshProgressFromCurrentColor() called on $this"
      )
    }

    progress = when (mode) {
      MODE_HUE -> {
        _currentColor.h
      }
      MODE_SATURATION -> {
        _currentColor.s
      }
      MODE_VALUE -> TODO()
      MODE_LIGHTNESS -> {
        _currentColor.l
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

  override fun toString(): String {
    return "HSLColorPickerSeekBar(tag = $tag, _mode=$_mode, _currentColor=$_currentColor)"
  }

  // Internal holder for non-calculated modes
  private var _currentColor = HSLColor()

  // TODO: Copy on read / copy on write?
  // TODO: To method?
  // TODO: Add support of missing components?
  var currentColor: HSLColor
    get() {
      return when (mode) {
        MODE_VALUE -> TODO()
        MODE_ALPHA -> TODO()
        else -> _currentColor.copy()
      }
    }
    set(value) {
      if (DEBUG) {
        Log.d(
          TAG,
          "currentColor set() called on $this with $value"
        )
      }
      // TODO: Revisit whether to place it here or under branches
      if (_currentColor == value) {
        return
      }
      _currentColor = value.copy()
      refreshProgressFromCurrentColor()
      refreshProgressDrawable()
      refreshThumb()
      notifyListenersOnColorChanged()
    }

  // TODO: Rename
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

    fun onColorChanged(
      picker: HSLColorPickerSeekBar,
      color: HSLColor,
      mode: Mode,
      value: Int
    )
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

    // BRIGHTNESS, V/B from HSV/HSB
    // TODO: Do we need this mode?
    MODE_VALUE(
      0,
      100
    ),

    // INTENSITY, L/I from HSL/HSI
    MODE_LIGHTNESS(
      0,
      100
    ),
    MODE_ALPHA(
      0,
      360
    )
  }
}
