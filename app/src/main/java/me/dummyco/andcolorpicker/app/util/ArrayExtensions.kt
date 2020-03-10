package me.dummyco.andcolorpicker.app.util

inline fun <reified R> Array<*>.firstIsInstance(): R {
  return filterIsInstanceTo(ArrayList<R>()).first()
}
