package codes.side.andcolorpicker

import codes.side.andcolorpicker.model.IntegerHSLColor

// TODO: Add foreach or smth like that
class PickerGroup : HSLColorPickerSeekBar.OnColorPickListener {
  // Kinda prioritized collection
  private val pickers = linkedSetOf<HSLColorPickerSeekBar>()

  fun setColor(color: IntegerHSLColor) {
    pickers.firstOrNull()?.currentColor = color
  }

  fun setColoringMode(coloringMode: HSLColorPickerSeekBar.ColoringMode) {
    pickers.forEach {
      it.coloringMode = coloringMode
    }
  }

  fun registerPicker(picker: HSLColorPickerSeekBar) {
    picker.addListener(this)
    pickers.add(picker)
    // Sync state on register
    notifyGroupOnBroadcastFrom(
      picker,
      picker.currentColor
    )
  }

  fun unregisterPicker(picker: HSLColorPickerSeekBar) {
    picker.removeListener(this)
    pickers.remove(picker)
  }

  override fun onColorPicking(
    picker: HSLColorPickerSeekBar,
    color: IntegerHSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int,
    fromUser: Boolean
  ) {
    notifyGroupOnBroadcastFrom(
      picker,
      color
    )
  }

  override fun onColorPicked(
    picker: HSLColorPickerSeekBar,
    color: IntegerHSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int,
    fromUser: Boolean
  ) {
    notifyGroupOnBroadcastFrom(
      picker,
      color
    )
  }

  override fun onColorChanged(
    picker: HSLColorPickerSeekBar,
    color: IntegerHSLColor,
    mode: HSLColorPickerSeekBar.Mode,
    value: Int
  ) {
    notifyGroupOnBroadcastFrom(
      picker,
      color
    )
  }

  private fun notifyGroupOnBroadcastFrom(
    picker: HSLColorPickerSeekBar,
    color: IntegerHSLColor
  ) {
    pickers.filter { it != picker }.forEach {
      it.currentColor = color
    }
  }
}
