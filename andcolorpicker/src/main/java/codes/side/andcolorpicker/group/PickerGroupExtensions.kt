package codes.side.andcolorpicker.group

import codes.side.andcolorpicker.ColorSeekBar
import codes.side.andcolorpicker.model.ColorModel

fun <C : ColorModel> PickerGroup<C>.registerPickers(vararg pickers: ColorSeekBar<C>) {
  registerPickers(listOf(*pickers))
}

fun <C : ColorModel> PickerGroup<C>.registerPickers(pickers: Iterable<ColorSeekBar<C>>) {
  pickers.forEach {
    registerPicker(it)
  }
}
