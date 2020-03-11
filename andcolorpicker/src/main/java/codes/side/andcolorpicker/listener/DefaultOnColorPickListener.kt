package codes.side.andcolorpicker.listener

import codes.side.andcolorpicker.ColorSeekBar
import codes.side.andcolorpicker.model.ColorModel

open class DefaultOnColorPickListener<C : ColorModel> :
  ColorSeekBar.OnColorPickListener<C> {
  override fun onColorPicking(
    picker: ColorSeekBar<C>,
    color: C,
    value: Int,
    fromUser: Boolean
  ) {

  }

  override fun onColorPicked(
    picker: ColorSeekBar<C>,
    color: C,
    value: Int,
    fromUser: Boolean
  ) {

  }

  override fun onColorChanged(
    picker: ColorSeekBar<C>,
    color: C,
    value: Int
  ) {

  }
}
