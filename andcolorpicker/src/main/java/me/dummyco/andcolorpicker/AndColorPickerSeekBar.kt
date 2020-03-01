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

class AndColorPickerSeekBar : AppCompatSeekBar,
    OnSeekBarChangeListener {

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

    progressDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.MAGENTA,
            Color.RED
        )
    )
        .also {
          it
              .setStroke(
                  resources
                      .getDimensionPixelOffset(R.dimen.acp_seek_background_padding),
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

    //thumbOffset -= 20

    refreshThumb()
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
            ColorUtils.HSLToColor(
                floatArrayOf(
                    progress.toFloat(),
                    1f,
                    0.5f
                )
            )
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
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
  }
}
