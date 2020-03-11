package codes.side.andcolorpicker.app.util

inline fun <reified R> Array<*>.firstIsInstanceOrNull(): R? {
  return filterIsInstanceTo(ArrayList<R>()).firstOrNull()
}
