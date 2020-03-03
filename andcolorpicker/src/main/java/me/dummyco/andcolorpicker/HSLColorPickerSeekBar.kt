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
import kotlin.math.roundToInt
import kotlin.random.Random

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
    const val H_INDEX = 0
    const val S_INDEX = 1
    const val L_INDEX = 2
    private const val DEFAULT_H = 0f
    private const val DEFAULT_S = 1f
    private const val DEFAULT_L = 0.5f
    private val DEFAULT_HSL_VALUES = floatArrayOf(
      DEFAULT_H,
      DEFAULT_S,
      DEFAULT_L
    )

    fun getDefaultHSLValueByIndex(index: Int): Float {
      return DEFAULT_HSL_VALUES[index]
    }
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
          paintDrawableStrokeSaturationHSLCache[H_INDEX] = _currentColor.h.toFloat()
          paintDrawableStrokeSaturationHSLCache[S_INDEX] = progress / mode.maxProgress.toFloat()
          paintDrawableStrokeSaturationHSLCache[L_INDEX] = DEFAULT_L
          ColorUtils.HSLToColor(paintDrawableStrokeSaturationHSLCache)
        }
        MODE_VALUE -> TODO()
        MODE_LIGHTNESS -> {
          paintDrawableStrokeLightnessHSLCache[H_INDEX] = _currentColor.h.toFloat()
          paintDrawableStrokeLightnessHSLCache[S_INDEX] = DEFAULT_S
          paintDrawableStrokeLightnessHSLCache[L_INDEX] =
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

  override fun setProgress(progress: Int) {
    super.setProgress(progress)
    //if (!isInitialized) {
    //  return
    //}
    //refreshCurrentColorFromProgress()
    //refreshProgressDrawable()
    //refreshThumb()
  }

  override fun setProgress(progress: Int, animate: Boolean) {
    super.setProgress(
      progress,
      animate
    )
    //if (!isInitialized) {
    //  return
    //}
    //refreshCurrentColorFromProgress()
    //refreshProgressDrawable()
    //refreshThumb()
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

  // TODO: Make integer-based and provide precision options
  class HSLColor {
    companion object {
      fun createRandomColor(): HSLColor {
        return HSLColor().setFromHSL(
          floatArrayOf(
            Random.Default.nextFloat() * 360f,
            Random.Default.nextFloat(),
            Random.Default.nextFloat()
          )
        )
      }
    }

    var h: Int
      get() {
        return values[H_INDEX]
      }
      set(value) {
        values[H_INDEX] = value.coerceIn(
          0,
          360
        )
      }
    var s: Int
      get() {
        return values[S_INDEX]
      }
      set(value) {
        values[S_INDEX] = value.coerceIn(
          0,
          100
        )
      }
    var l: Int
      get() {
        return values[L_INDEX]
      }
      set(value) {
        values[L_INDEX] = value.coerceIn(
          0,
          100
        )
      }
    private var _a: Int = 0
    var a: Int
      get() {
        return _a
      }
      set(value) {
        _a = value.coerceIn(
          0,
          100
        )
      }

    val colorInt: Int
      get() {
        return ColorUtils.HSLToColor(
          floatArrayOf(
            values[H_INDEX].toFloat(),
            values[S_INDEX] / 100f,
            values[L_INDEX] / 100f
          )
        )
      }
    private val _clearColorIntHSLCache = floatArrayOf(
      0f,
      0f,
      0f
    )
    val clearColorInt: Int
      get() {
        _clearColorIntHSLCache[H_INDEX] = h.toFloat()
        _clearColorIntHSLCache[S_INDEX] = DEFAULT_S
        _clearColorIntHSLCache[L_INDEX] = DEFAULT_L
        return ColorUtils.HSLToColor(_clearColorIntHSLCache)
      }

    private val values = intArrayOf(
      0,
      0,
      0
    )

    fun setFromHSL(h: Float, s: Float, l: Float): HSLColor {
      this.h = h.roundToInt()
      this.s = (s * 100f).roundToInt()
      this.l = (l * 100f).roundToInt()
      return this
    }

    fun setFromHSL(hsl: FloatArray): HSLColor {
      return setFromHSL(
        hsl[H_INDEX],
        hsl[S_INDEX],
        hsl[L_INDEX]
      )
    }

    fun setFromRGB(r: Int, g: Int, b: Int): HSLColor {
      val output = floatArrayOf(
        0f,
        0f,
        0f
      )
      ColorUtils.colorToHSL(
        Color.rgb(
          r,
          g,
          b
        ),
        output
      )
      setFromHSL(
        output
      )
      return this
    }

    fun setFromHSLColor(hslColor: HSLColor): HSLColor {
      hslColor.copyValuesTo(values)
      return this
    }

    // FIXME: Unsafe, provide checks
    fun copyValuesFrom(inValues: IntArray): HSLColor {
      inValues.copyInto(values)
      return this
    }

    fun copyValuesTo(outValues: IntArray) {
      values.copyInto(outValues)
    }

    fun copy(): HSLColor {
      return HSLColor().setFromHSLColor(this)
    }

    override fun toString(): String {
      return "HSLColor(a=$a, values=${values.contentToString()})"
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as HSLColor

      if (_a != other._a) return false
      if (!values.contentEquals(other.values)) return false

      return true
    }

    override fun hashCode(): Int {
      var result = _a.hashCode()
      result = 31 * result + values.contentHashCode()
      return result
    }
  }
}

class PickerGroup : HSLColorPickerSeekBar.OnColorPickListener {
  // Kinda prioritized collection
  private val pickers = linkedSetOf<HSLColorPickerSeekBar>()

  fun registerPicker(picker: HSLColorPickerSeekBar) {
    picker.addListener(this)
    pickers.add(picker)
    // Sync state on register
    notifyGroupOnBroadcastFrom(
      picker,
      picker.currentColor
    )
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
    notifyGroupOnBroadcastFrom(
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
    notifyGroupOnBroadcastFrom(
      picker,
      color
    )
  }

  override fun onColorChanged(
    picker: HSLColorPickerSeekBar,
    color: HSLColorPickerSeekBar.HSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int
  ) {
    notifyGroupOnBroadcastFrom(
      picker,
      color
    )
  }

  private fun notifyGroupOnBroadcastFrom(
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
