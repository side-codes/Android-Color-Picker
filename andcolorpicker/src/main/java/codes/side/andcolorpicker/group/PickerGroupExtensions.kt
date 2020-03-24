package codes.side.andcolorpicker.group

import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.view.picker.ColorSeekBar

fun <C : Color> PickerGroup<C>.registerPickers(vararg pickers: ColorSeekBar<C>) {
  registerPickers(listOf(*pickers))
}

fun <C : Color> PickerGroup<C>.registerPickers(pickers: Iterable<ColorSeekBar<C>>) {
  pickers.forEach {
    registerPicker(it)
  }
}
