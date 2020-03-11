package codes.side.andcolorpicker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.StateSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import codes.side.andcolorpicker.model.ColorModel

abstract class ColorSeekBar<C : ColorModel> : AppCompatSeekBar,
  SeekBar.OnSeekBarChangeListener {

  companion object {
    private const val TAG = "ColorSeekBar"
    private const val DEBUG = false
  }

  abstract var currentColor: C

  private val colorPickListeners = hashSetOf<OnColorPickListener<C>>()
  private lateinit var thumbDrawableDefaultWrapper: LayerDrawable
  private lateinit var thumbDrawablePressed: GradientDrawable

  // TODO: Rename
  protected val coloringDrawables = hashSetOf<Drawable>()

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
    splitTrack = false
    setOnSeekBarChangeListener(this)

    setupBackground()
    setupProgressDrawable()
    setupThumb()
  }

  private fun setupBackground() {
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
  }

  private fun setupThumb() {
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
  }

  open fun addListener(listener: OnColorPickListener<C>) {
    colorPickListeners.add(listener)
  }

  open fun removeListener(listener: OnColorPickListener<C>) {
    colorPickListeners.remove(listener)
  }

  fun clearListeners() {
    colorPickListeners.clear()
  }

  override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
    if (l != this) {
      throw IllegalStateException("Custom OnSeekBarChangeListener not supported yet")
    }
    super.setOnSeekBarChangeListener(l)
  }

  protected fun notifyListenersOnColorChanged() {
    colorPickListeners.forEach {
      it.onColorChanged(
        this,
        currentColor,
        progress
      )
    }
  }

  protected fun notifyListenersOnColorPicking(fromUser: Boolean) {
    colorPickListeners.forEach {
      it.onColorPicking(
        this,
        currentColor,
        progress,
        fromUser
      )
    }
  }

  protected fun notifyListenersOnColorPicked(fromUser: Boolean) {
    colorPickListeners.forEach {
      it.onColorPicked(
        this,
        currentColor,
        progress,
        fromUser
      )
    }
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    colorPickListeners.forEach {
      it.onColorPicked(
        this,
        currentColor,
        progress,
        true
      )
    }
  }

  // TODO: Rename
  interface OnColorPickListener<C : ColorModel> {
    fun onColorPicking(
      picker: ColorSeekBar<C>,
      color: C,
      value: Int,
      fromUser: Boolean
    )

    fun onColorPicked(
      picker: ColorSeekBar<C>,
      color: C,
      value: Int,
      fromUser: Boolean
    )

    fun onColorChanged(
      picker: ColorSeekBar<C>,
      color: C,
      value: Int
    )
  }
}
