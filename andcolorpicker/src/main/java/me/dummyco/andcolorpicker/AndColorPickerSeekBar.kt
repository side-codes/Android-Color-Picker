package me.dummyco.andcolorpicker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AnimatedStateListDrawable
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.GradientDrawable
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

  lateinit var thumbInnerDrawable: GradientDrawable

  private fun init() {
    setOnSeekBarChangeListener(this)

    splitTrack = false

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
    ).also {
      it.setStroke(
          30,
          Color.TRANSPARENT
      )
      it.cornerRadius = resources.getDimensionPixelOffset(R.dimen.acp_seek_background_corner_radius)
          .toFloat()
      it.shape = GradientDrawable.RECTANGLE
    }

    val thumbSizePx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_size)

    thumbInnerDrawable = GradientDrawable().also {
      it.color = ColorStateList.valueOf(Color.WHITE)
      it.shape = GradientDrawable.OVAL
      it.setSize(
          thumbSizePx,
          thumbSizePx
      )
    }

    // TODO: Try to use ScaleDrawable
    thumb = AnimatedStateListDrawable().also { animatedStateListDrawable ->
      animatedStateListDrawable.addState(
          intArrayOf(android.R.attr.state_pressed),
          GradientDrawable().also {
            it.color = ColorStateList.valueOf(Color.BLUE)
            it.shape = GradientDrawable.OVAL
            it.setSize(
                thumbSizePx * 2,
                thumbSizePx * 2
            )
          },
          1
      )
      animatedStateListDrawable.addState(
          StateSet.WILD_CARD,
          thumbInnerDrawable,
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

    thumbOffset -= 20

    refreshThumb()
  }

  private fun refreshThumb() {
    val thumbStrokeWidthPx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_stroke_width)

    thumbInnerDrawable.setStroke(
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
    super.onDraw(canvas)
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
