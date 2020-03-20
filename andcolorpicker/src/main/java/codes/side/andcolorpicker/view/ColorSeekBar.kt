package codes.side.andcolorpicker.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.StateSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.ColorConverter
import codes.side.andcolorpicker.converter.ColorConverterHub
import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.factory.ColorFactory
import codes.side.andcolorpicker.util.marker

// TODO: Support alpha on that level?
// TODO: Split on gradient-based and custom inheritance tree
// TODO: Make color not generic, but bridge component
// CONTRACT: Make sure to place all needed state checks on virtual methods and refresh in init if needed
@Suppress(
  "ConstantConditionIf",
  "LeakingThis"
)
abstract class ColorSeekBar<C : Color> @JvmOverloads constructor(
  private val colorFactory: ColorFactory<C>,
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = androidx.appcompat.R.attr.seekBarStyle
) :
  AppCompatSeekBar(
    context,
    attrs,
    defStyle
  ),
  SeekBar.OnSeekBarChangeListener {

  companion object {
    private const val TAG = "ColorSeekBar"
    private const val DEBUG = false
  }

  // TODO: Revisit factory-based approach
  private val _pickedColor: C = colorFactory.create()
  var pickedColor: C
    get() {
      return colorFactory.createColorFrom(_pickedColor)
    }
    set(value) {
      if (DEBUG) {
        Log.d(
          TAG,
          "currentColor set() called on $this with $value"
        )
      }
      if (_pickedColor == value) {
        return
      }
      updateInternalPickedColorFrom(value)
      refreshProgressFromCurrentColor()
      refreshProgressDrawable()
      refreshThumb()
      notifyListenersOnColorChanged()
    }

  protected val internalPickedColor: C
    get() {
      return _pickedColor
    }

  protected open val colorConverter: ColorConverter
    get() {
      return ColorConverterHub.getConverterByKey(internalPickedColor.colorKey)
    }

  var notifyListeners = true

  // Dirty hack to stop onProgressChanged while playing with min/max
  private var minUpdating = false
  private var maxUpdating = false

  private val colorPickListeners = hashSetOf<OnColorPickListener<ColorSeekBar<C>, C>>()
  private lateinit var thumbDrawableDefaultWrapper: LayerDrawable
  private lateinit var thumbDrawablePressed: GradientDrawable

  // TODO: Rename
  protected val coloringDrawables = hashSetOf<Drawable>()

  init {
    splitTrack = false

    setOnSeekBarChangeListener(this)

    setupBackground()
    setupProgressDrawable()
    setupThumb()

    refreshProperties()
    refreshProgressFromCurrentColor()
    refreshProgressDrawable()
    refreshThumb()
  }

  private fun setupBackground() {
    background = background.mutate()
      .also {
        if (it is RippleDrawable) {
          // TODO: Set ripple size for pre-M too
          // TODO: Make ripple size configurable?
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

    val progressPaddingPx = resources.getDimensionPixelOffset(R.dimen.acp_seek_progress_padding)
    val progressHeightPx = resources.getDimensionPixelOffset(R.dimen.acp_seek_progress_height)

    val layers = onSetupProgressDrawableLayers(arrayOf())
    progressDrawable = LayerDrawable(
      layers
    ).also {
      // We're limited to insets on API 21
      // Migrate to layer height / padding on API 23+
      // TODO: Allow to configure whether to use insets or not
      layers.forEachIndexed { index, _ ->
        it.setLayerInset(
          index,
          progressPaddingPx,
          progressPaddingPx,
          progressPaddingPx,
          progressPaddingPx
        )
      }
    }
  }

  // Immutable array to limit hacks
  // Inherited layers may be not fully built by this method invocation time
  protected abstract fun onSetupProgressDrawableLayers(layers: Array<Drawable>): Array<Drawable>

  private fun setupThumb() {
    val backgroundPaddingPx = resources.getDimensionPixelOffset(R.dimen.acp_seek_progress_padding)
    val thumbFullSizePx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_size_full)
    val thumbDefaultSizePx = resources.getDimensionPixelOffset(R.dimen.acp_thumb_size_default)

    val sizeD = thumbFullSizePx - thumbDefaultSizePx
    val sizeDHalf = sizeD / 2

    thumbDrawableDefaultWrapper = LayerDrawable(
      arrayOf(
        GradientDrawable().also {
          it.color = ColorStateList.valueOf(android.graphics.Color.WHITE)
          it.shape = GradientDrawable.OVAL
          it.setSize(
            thumbDefaultSizePx,
            thumbDefaultSizePx
          )
        }
      )
    ).also {
      // We're limited to insets on API 21
      // Migrate to layer height / padding on API 23+
      it.setLayerInset(
        0,
        sizeDHalf,
        sizeDHalf,
        sizeDHalf,
        sizeDHalf
      )
    }

    thumbDrawablePressed = GradientDrawable().also {
      it.color = ColorStateList.valueOf(android.graphics.Color.WHITE)
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

  override fun setMin(min: Int) {
    ::minUpdating.marker {
      super.setMin(min)
    }
  }

  override fun setMax(max: Int) {
    ::maxUpdating.marker {
      super.setMax(max)
    }
  }

  // TODO: Make abstract? Make template?
  protected open fun updateInternalPickedColorFrom(value: C) {
    if (DEBUG) {
      Log.d(
        TAG,
        "updateInternalCurrentColorFrom() called on $this"
      )
    }
  }

  // TODO: Make abstract? Make template?
  protected open fun refreshProperties() {
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshProperties() called on $this"
      )
    }
  }

  // TODO: Make abstract? Make template?
  protected open fun refreshProgressFromCurrentColor() {
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshProgressFromCurrentColor() called on $this"
      )
    }
  }

  // TODO: Make abstract? Make template?
  protected open fun refreshInternalPickedColorFromProgress() {
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshInternalCurrentColorFromProgress() called on $this"
      )
    }
  }

  // TODO: Make abstract? Make template?
  protected open fun refreshProgressDrawable() {
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshProgressDrawable() called on $this"
      )
    }
  }

  /**
   * CONTRACT: Should paint GradientDrawable and first layer of LayerDrawable
   */
  // TODO: Make abstract? Make template?
  // TODO: Pass ready-to-paint drawables list?
  protected open fun refreshThumb() {
    if (DEBUG) {
      Log.d(
        TAG,
        "refreshThumb() called on $this"
      )
    }
  }

  fun addListener(listener: OnColorPickListener<ColorSeekBar<C>, C>) {
    colorPickListeners.add(listener)
  }

  fun removeListener(listener: OnColorPickListener<ColorSeekBar<C>, C>) {
    colorPickListeners.remove(listener)
  }

  fun clearListeners() {
    colorPickListeners.clear()
  }

  // TODO: Add (mask) delegating OnSeekBarChangeListener
  final override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
    if (l != this) {
      throw IllegalStateException("Custom OnSeekBarChangeListener not supported yet")
    }
    super.setOnSeekBarChangeListener(l)
  }

  protected fun notifyListenersOnColorChanged() {
    if (!notifyListeners) {
      if (DEBUG) {
        Log.d(
          TAG,
          "Listeners silenced, but notifyListenersOnColorChanged called"
        )
      }
      return
    }

    colorPickListeners.forEach {
      it.onColorChanged(
        this,
        pickedColor,
        progress
      )
    }
  }

  private fun notifyListenersOnColorPicking(fromUser: Boolean) {
    if (!notifyListeners) {
      if (DEBUG) {
        Log.d(
          TAG,
          "Listeners silenced, but notifyListenersOnColorPicking called"
        )
      }
      return
    }

    colorPickListeners.forEach {
      it.onColorPicking(
        this,
        pickedColor,
        progress,
        fromUser
      )
    }
  }

  private fun notifyListenersOnColorPicked(fromUser: Boolean) {
    if (!notifyListeners) {
      if (DEBUG) {
        Log.d(
          TAG,
          "Listeners silenced, but notifyListenersOnColorPicked called"
        )
      }
      return
    }

    colorPickListeners.forEach {
      it.onColorPicked(
        this,
        pickedColor,
        progress,
        fromUser
      )
    }
  }

  // TODO: Revisit
  override fun onProgressChanged(
    seekBar: SeekBar,
    progress: Int,
    fromUser: Boolean
  ) {
    if (minUpdating || maxUpdating) {
      return
    }

    refreshInternalPickedColorFromProgress()
    refreshProgressDrawable()
    refreshThumb()
    notifyListenersOnColorPicking(fromUser)

    if (!fromUser) {
      notifyListenersOnColorPicked(fromUser)
    }
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    notifyListenersOnColorPicked(true)
  }

  // TODO: Rename
  interface OnColorPickListener<S : ColorSeekBar<C>, C : Color> {
    fun onColorPicking(
      picker: S,
      color: C,
      value: Int,
      fromUser: Boolean
    )

    fun onColorPicked(
      picker: S,
      color: C,
      value: Int,
      fromUser: Boolean
    )

    fun onColorChanged(
      picker: S,
      color: C,
      value: Int
    )
  }

  open class DefaultOnColorPickListener<S : ColorSeekBar<C>, C : Color> :
    OnColorPickListener<S, C> {
    override fun onColorPicking(
      picker: S,
      color: C,
      value: Int,
      fromUser: Boolean
    ) {

    }

    override fun onColorPicked(
      picker: S,
      color: C,
      value: Int,
      fromUser: Boolean
    ) {

    }

    override fun onColorChanged(
      picker: S,
      color: C,
      value: Int
    ) {

    }
  }
}
