package codes.side.andcolorpicker.alpha

import android.content.Context
import android.util.AttributeSet
import codes.side.andcolorpicker.ColorSeekBar
import codes.side.andcolorpicker.converter.HSLColorConverter
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.model.factory.HSLColorFactory

class HSLAlphaColorPickerSeekBar :
  AlphaColorPickerSeekBar<IntegerHSLColor> {

  override val colorConverter: HSLColorConverter
    get() = super.colorConverter as HSLColorConverter

  constructor(context: Context) : super(
    HSLColorFactory(),
    context
  )

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : super(
    HSLColorFactory(),
    context,
    attrs
  )

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(
    HSLColorFactory(),
    context,
    attrs,
    defStyleAttr
  )

  override fun updateInternalCurrentColorFrom(value: IntegerHSLColor) {
    super.updateInternalCurrentColorFrom(value)
    internalPickedColor.setFromHSLColor(value)
  }

  override fun refreshProperties() {
    super.refreshProperties()
    // TODO: Pull to constants
    max = 100
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
