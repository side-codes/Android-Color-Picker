package codes.side.andcolorpicker

import codes.side.andcolorpicker.model.IntegerHSLColor

open class DefaultOnColorPickListener : HSLColorPickerSeekBar.OnColorPickListener {
  override fun onColorPicking(
    picker: HSLColorPickerSeekBar,
    color: IntegerHSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int,
    fromUser: Boolean
  ) {

  }

  override fun onColorPicked(
    picker: HSLColorPickerSeekBar,
    color: IntegerHSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int,
    fromUser: Boolean
  ) {

  }

  override fun onColorChanged(
    picker: HSLColorPickerSeekBar,
    color: IntegerHSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int
  ) {

  }
}
