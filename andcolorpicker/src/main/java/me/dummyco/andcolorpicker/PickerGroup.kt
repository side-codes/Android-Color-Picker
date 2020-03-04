package me.dummyco.andcolorpicker

import me.dummyco.andcolorpicker.model.DiscreteHSLColor

class PickerGroup : HSLColorPickerSeekBar.OnColorPickListener {
  // Kinda prioritized collection
  private val pickers = linkedSetOf<HSLColorPickerSeekBar>()

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
    color: DiscreteHSLColor,
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
    color: DiscreteHSLColor,
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
    color: DiscreteHSLColor,
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
    color: DiscreteHSLColor
  ) {
    pickers.filter { it != picker }.forEach {
      it.currentColor = color
    }
  }
}
