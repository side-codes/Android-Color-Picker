package me.dummyco.andcolorpicker

fun PickerGroup.registerPickers(vararg pickers: HSLColorPickerSeekBar) {
  registerPickers(listOf(*pickers))
}

fun PickerGroup.registerPickers(pickers: Iterable<HSLColorPickerSeekBar>) {
  pickers.forEach {
    registerPicker(it)
  }
}
