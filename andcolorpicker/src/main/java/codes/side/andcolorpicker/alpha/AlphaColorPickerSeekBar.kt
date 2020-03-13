package codes.side.andcolorpicker.alpha

import android.content.Context
import android.util.AttributeSet
import codes.side.andcolorpicker.ColorSeekBar
import codes.side.andcolorpicker.model.ColorModel
import codes.side.andcolorpicker.model.factory.ColorFactory

// TODO: Think on making that non-abstract
abstract class AlphaColorPickerSeekBar<C : ColorModel> : ColorSeekBar<C> {
  constructor(colorFactory: ColorFactory<C>, context: Context) : super(
    colorFactory,
    context
  )

  constructor(
    colorFactory: ColorFactory<C>,
    context: Context,
    attrs: AttributeSet?
  ) : super(
    colorFactory,
    context,
    attrs
  )

  constructor(
    colorFactory: ColorFactory<C>,
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(
    colorFactory,
    context,
    attrs,
    defStyleAttr
  )
}
