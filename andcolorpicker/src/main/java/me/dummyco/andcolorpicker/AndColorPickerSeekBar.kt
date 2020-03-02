package me.dummyco.andcolorpicker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AnimatedStateListDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.util.StateSet
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.graphics.ColorUtils
import me.dummyco.andcolorpicker.AndColorPickerSeekBar.Mode.MODE_ALPHA
import me.dummyco.andcolorpicker.AndColorPickerSeekBar.Mode.MODE_HUE
import me.dummyco.andcolorpicker.AndColorPickerSeekBar.Mode.MODE_LIGHTNESS
import me.dummyco.andcolorpicker.AndColorPickerSeekBar.Mode.MODE_SATURATION
import me.dummyco.andcolorpicker.AndColorPickerSeekBar.Mode.MODE_VALUE
import kotlin.math.roundToInt

class AndColorPickerSeekBar : AppCompatSeekBar,
                              OnSeekBarChangeListener {
  companion object {
    private const val TAG = "AndColorPickerSeekBar"
  }

  var colorPickListener: OnColorPickListener? = null

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

  // TODO: Wrap props in enum
  private fun refreshProperties() {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      min = mode.min
    }
    max = mode.max

    when (mode) {
      MODE_HUE        -> {
      }
      MODE_SATURATION -> {
      }
      MODE_VALUE      -> TODO()
      MODE_LIGHTNESS  -> TODO()
      MODE_ALPHA      -> TODO()
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
        arrayOf(GradientDrawable().also {
          it.color = ColorStateList.valueOf(Color.TRANSPARENT)
          it.shape = GradientDrawable.OVAL
          it.setSize(
              thumbFullSizePx,
              thumbFullSizePx
          )
        },
            GradientDrawable().also {
              it.color = ColorStateList.valueOf(Color.WHITE)
              it.shape = GradientDrawable.OVAL
              it.setSize(
                  thumbDefaultSizePx,
                  thumbDefaultSizePx
              )
            })
    ).also {
      it.setLayerInset(
          1,
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

    progressDrawable = GradientDrawable().also {
      it.orientation = GradientDrawable.Orientation.LEFT_RIGHT
      it.setStroke(
          backgroundPaddingPx,
          Color.TRANSPARENT
      )
      it.cornerRadius = resources.getDimensionPixelOffset(R.dimen.acp_seek_background_corner_radius)
          .toFloat()
      it.shape = GradientDrawable.RECTANGLE
    }

    refreshProgressDrawable()
  }

  private fun refreshProgressDrawable() {
    (progressDrawable as GradientDrawable).colors = when (mode) {
      MODE_HUE        -> {
        intArrayOf(
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.MAGENTA,
            Color.RED
        )
      }
      MODE_SATURATION -> {
        intArrayOf(
            Color.rgb(
                128,
                128,
                128
            ),
            // TODO: Use color
            currentColor
        )
      }
      MODE_VALUE      -> TODO()
      MODE_LIGHTNESS  -> TODO()
      MODE_ALPHA      -> TODO()
    }
  }

  private fun refreshThumb() {
    coloringDrawables.forEach {
      when (it) {
        is GradientDrawable -> {
          paintDrawableStroke(it)
        }
        is LayerDrawable    -> {
          paintDrawableStroke(it.getDrawable(1) as GradientDrawable)
        }
      }
    }
  }

  private fun paintDrawableStroke(drawable: GradientDrawable) {
    val thumbStrokeWidthPx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_stroke_width)

    drawable.setStroke(
        thumbStrokeWidthPx,
        when (mode) {
          MODE_HUE        -> currentColor
          MODE_SATURATION -> {
            val hsl = floatArrayOf(
                0f,
                0f,
                0f
            )
            ColorUtils.colorToHSL(
                currentColor,
                hsl
            )
            hsl[1] = progress / 100f
            ColorUtils.HSLToColor(hsl)
          }
          MODE_VALUE      -> TODO()
          MODE_LIGHTNESS  -> TODO()
          MODE_ALPHA      -> TODO()
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
    refreshThumb()
    colorPickListener?.onColorPicking(
        currentColor,
        fromUser
    )
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    colorPickListener?.onColorPicked(
        currentColor,
        true
    )
  }

  // Internal holder for non-calculated modes
  private var _currentColor: Int = 0
  private val currentColorWriteHSLCache = floatArrayOf(
      0f,
      0f,
      0f
  )
  private val currentColorReadHSLCache = floatArrayOf(
      0f,
      1f,
      0.5f
  )
  // TODO: Create Color wrapper class with components
  // Is it composed color or reference color? Should I split them?
  @get:ColorInt
  var currentColor: Int
    get() {
      return when (mode) {
        MODE_HUE        -> {
          currentColorReadHSLCache[0] = progress.toFloat()
          ColorUtils.HSLToColor(currentColorReadHSLCache)
        }
        MODE_SATURATION -> {
          _currentColor
        }
        MODE_VALUE      -> TODO()
        MODE_LIGHTNESS  -> TODO()
        MODE_ALPHA      -> TODO()
      }
    }
    set(value) {
      // TODO: Revisit whether to place it here or under branches
      _currentColor = value

      when (mode) {
        MODE_HUE        -> {
          ColorUtils.colorToHSL(
              value,
              currentColorWriteHSLCache
          )
          progress = currentColorWriteHSLCache[0].roundToInt()
        }
        MODE_SATURATION -> {

        }
        MODE_VALUE      -> TODO()
        MODE_LIGHTNESS  -> TODO()
        MODE_ALPHA      -> TODO()
      }

      refreshProgressDrawable()
      refreshThumb()
    }

  interface OnColorPickListener {
    fun onColorPicking(
        @ColorInt
        color: Int, fromUser: Boolean
    )

    fun onColorPicked(
        @ColorInt
        color: Int, fromUser: Boolean
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
    MODE_LIGHTNESS(
        0,
        100
    ), // INTENSITY, L/I from HSL/HSI
    MODE_ALPHA(
        0,
        360
    )
  }
}
