package codes.side.andcolorpicker.alpha

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import codes.side.andcolorpicker.converter.HSLColorConverter
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.model.factory.HSLColorFactory
import codes.side.andcolorpicker.view.ColorSeekBar

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

  override val colorConverter: HSLColorConverter
    get() = super.colorConverter as HSLColorConverter

  override fun refreshProgressDrawable() {
    super.refreshProgressDrawable()

    ((progressDrawable as LayerDrawable).getDrawable(1) as GradientDrawable).colors =
      intArrayOf(
        android.graphics.Color.TRANSPARENT,
        colorConverter.convertToPureColorInt(internalPickedColor)
      )
  }

  override fun updateInternalPickedColorFrom(value: IntegerHSLColor) {
    super.updateInternalPickedColorFrom(value)
    internalPickedColor.setFromHSLColor(value)
  }

  override fun refreshInternalPickedColorFromProgress() {
    super.refreshInternalPickedColorFromProgress()
    internalPickedColor.intA = progress
  }

  override fun refreshProgressFromCurrentColor() {
    super.refreshProgressFromCurrentColor()
    progress = internalPickedColor.intA
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
