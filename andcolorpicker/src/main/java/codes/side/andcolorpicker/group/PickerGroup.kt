package codes.side.andcolorpicker.group

import codes.side.andcolorpicker.ColorSeekBar
import codes.side.andcolorpicker.model.ColorModel

// TODO: Add foreach or smth like that
open class PickerGroup<C : ColorModel> : Iterable<ColorSeekBar<C>>,
  ColorSeekBar.OnColorPickListener<C> {
  // Kinda prioritized collection
  private val pickers = linkedSetOf<ColorSeekBar<C>>()

  fun setColor(color: C) {
    pickers.firstOrNull()?.currentColor = color
  }

  fun registerPicker(picker: ColorSeekBar<C>) {
    picker.addListener(this)
    pickers.add(picker)
    // Sync state on register
    notifyGroupOnBroadcastFrom(
      picker,
      picker.currentColor
    )
  }

  fun unregisterPicker(picker: ColorSeekBar<C>) {
    picker.removeListener(this)
    pickers.remove(picker)
  }

  override fun onColorPicking(
    picker: ColorSeekBar<C>,
    color: C,
    value: Int,
    fromUser: Boolean
  ) {
    notifyGroupOnBroadcastFrom(
      picker,
      color
    )
  }

  override fun onColorPicked(
    picker: ColorSeekBar<C>,
    color: C,
    value: Int,
    fromUser: Boolean
  ) {
    notifyGroupOnBroadcastFrom(
      picker,
      color
    )
  }

  override fun onColorChanged(
    picker: ColorSeekBar<C>,
    color: C,
    value: Int
  ) {
    notifyGroupOnBroadcastFrom(
      picker,
      color
    )
  }

  private fun notifyGroupOnBroadcastFrom(
    picker: ColorSeekBar<C>,
    color: C
  ) {
    pickers.filter { it != picker }.forEach {
      it.currentColor = color
    }
  }

  override fun iterator(): Iterator<ColorSeekBar<C>> {
    return pickers.iterator()
  }
}
