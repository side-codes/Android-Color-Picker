package me.dummyco.andcolorpicker.app

inline fun <reified R> Array<*>.firstIsInstance(): R {
  return filterIsInstanceTo(ArrayList<R>()).first()
}
