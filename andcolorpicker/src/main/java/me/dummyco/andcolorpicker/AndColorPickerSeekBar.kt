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
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

class AndColorPickerSeekBar : AppCompatSeekBar,
    OnSeekBarChangeListener {
  var mode: Mode = Mode
      .HUE
  var colorPickListener: OnColorPickListener? = null

  companion object {

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

  lateinit var thumbDrawableDefaultWrapper: LayerDrawable
  lateinit var thumbDrawablePressed: GradientDrawable
  private val coloringDrawables = hashSetOf<Drawable>()

  private fun init() {
    setOnSeekBarChangeListener(this)

    splitTrack = false

    background = background
        .mutate()
        .also {
          if (it is RippleDrawable) {
            // TODO: Set ripple size for pre-M too
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              val rippleSizePx = resources
                  .getDimensionPixelOffset(R.dimen.acp_thumb_ripple_radius)
              it
                  .radius = rippleSizePx
            }
          }
        }

    setupProgressDrawable()

    val backgroundPaddingPx = resources
        .getDimensionPixelOffset(R.dimen.acp_seek_background_padding)
    val thumbFullSizePx = resources
        .getDimensionPixelOffset(R.dimen.acp_thumb_size_full)
    val thumbDefaultSizePx = resources
        .getDimensionPixelOffset(R.dimen.acp_thumb_size_default)

    val sizeD = thumbFullSizePx - thumbDefaultSizePx
    val sizeDHalf = sizeD / 2

    thumbDrawableDefaultWrapper = LayerDrawable(
        arrayOf(
            GradientDrawable().also {
              it
                  .color = ColorStateList
                  .valueOf(Color.TRANSPARENT)
              it
                  .shape = GradientDrawable
                  .OVAL
              it
                  .setSize(
                      thumbFullSizePx,
                      thumbFullSizePx
                  )
            },
            GradientDrawable().also {
              it
                  .color = ColorStateList
                  .valueOf(Color.WHITE)
              it
                  .shape = GradientDrawable
                  .OVAL
              it
                  .setSize(
                      thumbDefaultSizePx,
                      thumbDefaultSizePx
                  )
            }
        )
    )
        .also {
          it
              .setLayerInset(1, sizeDHalf, sizeDHalf, sizeDHalf, sizeDHalf)
        }

    thumbDrawablePressed = GradientDrawable()
        .also {
          it
              .color = ColorStateList
              .valueOf(Color.WHITE)
          it
              .shape = GradientDrawable
              .OVAL
          it
              .setSize(
                  thumbFullSizePx,
                  thumbFullSizePx
              )
        }

    coloringDrawables
        .add(thumbDrawableDefaultWrapper)
    coloringDrawables
        .add(thumbDrawablePressed)

    thumb = AnimatedStateListDrawable()
        .also { animatedStateListDrawable ->
          animatedStateListDrawable
              .addState(
                  intArrayOf(android.R.attr.state_pressed),
                  thumbDrawablePressed,
                  1
              )
          animatedStateListDrawable
              .addState(
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

    refreshThumb()
  }

  private fun setupProgressDrawable() {
    val backgroundPaddingPx = resources
        .getDimensionPixelOffset(R.dimen.acp_seek_background_padding)

    progressDrawable = GradientDrawable()
        .also {
          it
              .orientation = GradientDrawable
              .Orientation
              .LEFT_RIGHT
          it
              .setStroke(
                  backgroundPaddingPx,
                  Color.TRANSPARENT
              )
          it
              .cornerRadius = resources
              .getDimensionPixelOffset(R.dimen.acp_seek_background_corner_radius)
              .toFloat()
          it
              .shape = GradientDrawable
              .RECTANGLE
        }

    refreshProgressDrawable()
  }

  private fun refreshProgressDrawable() {
    (progressDrawable as GradientDrawable)
        .colors = when (mode) {
      Mode.HUE -> {
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
      Mode.SATURATION -> TODO()
      Mode.VALUE -> TODO()
      Mode.LIGHTNESS -> TODO()
      Mode.ALPHA -> TODO()
    }
  }

  private fun refreshThumb() {
    coloringDrawables
        .forEach {
          when (it) {
            is GradientDrawable -> {
              paintDrawableStroke(it)
            }
            is LayerDrawable -> {
              paintDrawableStroke(it.getDrawable(1) as GradientDrawable)
            }
          }
        }
  }

  private fun paintDrawableStroke(drawable: GradientDrawable) {
    val thumbStrokeWidthPx = resources
        .getDimensionPixelOffset(R.dimen.acp_thumb_stroke_width)

    drawable
        .setStroke(
            thumbStrokeWidthPx,
            currentColor
        )
  }

  override fun onDraw(canvas: Canvas) {
    super
        .onDraw(canvas)
  }

  override fun onProgressChanged(
      seekBar: SeekBar,
      progress: Int,
      fromUser: Boolean
  ) {
    refreshThumb()
    colorPickListener
        ?.onColorPicking(currentColor)
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {

  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    colorPickListener
        ?.onColorPicked(currentColor)
  }

  // TODO: Create Color wrapper class
  @get:ColorInt
  var currentColor: Int
    get() {
      return ColorUtils
          .HSLToColor(
              floatArrayOf(
                  progress.toFloat(),
                  1f,
                  0.5f
              )
          )
    }
    set(value) {
      when (mode) {
        Mode.HUE -> {
          // TODO: Cache
          val output = floatArrayOf(0f, 0f, 0f)
          ColorUtils
              .colorToHSL(
                  value,
                  output
              )
          progress = output[0]
              .roundToInt()
        }
        Mode.SATURATION -> TODO()
        Mode.VALUE -> TODO()
        Mode.LIGHTNESS -> TODO()
        Mode.ALPHA -> TODO()
      }
    }

  interface OnColorPickListener {
    fun onColorPicking(@ColorInt color: Int)

    fun onColorPicked(@ColorInt color: Int)
  }

  enum class Mode {
    HUE,
    SATURATION,
    VALUE,//BRIGHTNESS
    LIGHTNESS,
    ALPHA
  }
}
