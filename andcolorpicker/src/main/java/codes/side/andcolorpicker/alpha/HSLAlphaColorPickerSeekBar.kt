package codes.side.andcolorpicker.alpha

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.IntegerHSLColorConverter
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.model.factory.HSLColorFactory
import codes.side.andcolorpicker.view.ColorSeekBar

// TODO: Add modes support
class HSLAlphaColorPickerSeekBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = androidx.appcompat.R.attr.seekBarStyle
) :
  AlphaColorPickerSeekBar<IntegerHSLColor>(
    HSLColorFactory(),
    context,
    attrs,
    defStyle
  ) {

  private var isInitialized = false

  private val thumbStrokeWidthPx by lazy {
    resources.getDimensionPixelOffset(R.dimen.acp_thumb_stroke_width)
  }

  override val colorConverter: IntegerHSLColorConverter
    get() = super.colorConverter as IntegerHSLColorConverter

  init {
    isInitialized = true
    refreshProperties()
  }

  override fun refreshProperties() {
    super.refreshProperties()
    // TODO: Pull to constants
    max = 255
  }

  override fun refreshProgressDrawable() {
    super.refreshProgressDrawable()

    ((progressDrawable as LayerDrawable).getDrawable(1) as GradientDrawable).colors =
      intArrayOf(
        android.graphics.Color.TRANSPARENT,
        colorConverter.convertToOpaqueColorInt(internalPickedColor)
      )
  }

  override fun updateInternalPickedColorFrom(value: IntegerHSLColor) {
    super.updateInternalPickedColorFrom(value)
    internalPickedColor.setFrom(value)
  }

  override fun refreshInternalPickedColorFromProgress() {
    super.refreshInternalPickedColorFromProgress()
    val currentProgress = progress
    val currentA = internalPickedColor.intA
    val changed = if (currentA != currentProgress) {
      internalPickedColor.intA = progress
      true
    } else {
      false
    }

    if (changed) {
      notifyListenersOnColorChanged()
    }
  }

  override fun refreshProgressFromCurrentColor() {
    super.refreshProgressFromCurrentColor()
    progress = internalPickedColor.intA
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

  private fun paintThumbStroke(drawable: GradientDrawable) {
    if (!isInitialized) {
      return
    }

    drawable.setStroke(
      thumbStrokeWidthPx,
      colorConverter.convertToColorInt(internalPickedColor)
    )
  }

  interface OnColorPickListener :
    ColorSeekBar.OnColorPickListener<HSLAlphaColorPickerSeekBar, IntegerHSLColor>

  open class DefaultOnColorPickListener : OnColorPickListener {
    override fun onColorPicking(
      picker: HSLAlphaColorPickerSeekBar,
      color: IntegerHSLColor,
      value: Int,
      fromUser: Boolean
    ) {

    }

    override fun onColorPicked(
      picker: HSLAlphaColorPickerSeekBar,
      color: IntegerHSLColor,
      value: Int,
      fromUser: Boolean
    ) {

    }

    override fun onColorChanged(
      picker: HSLAlphaColorPickerSeekBar,
      color: IntegerHSLColor,
      value: Int
    ) {

    }
  }
}
