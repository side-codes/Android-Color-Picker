package codes.side.andcolorpicker.alpha

import android.content.Context
import android.util.AttributeSet
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.model.factory.HSLColorFactory

class HSLAlphaColorPickerSeekBar : AlphaColorPickerSeekBar<IntegerHSLColor> {
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
}
