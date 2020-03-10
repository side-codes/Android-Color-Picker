package me.dummyco.andcolorpicker.app.util

inline fun <reified R> Array<*>.firstIsInstanceOrNull(): R? {
  return filterIsInstanceTo(ArrayList<R>()).firstOrNull()
}
