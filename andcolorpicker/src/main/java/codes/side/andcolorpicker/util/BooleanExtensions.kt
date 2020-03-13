package codes.side.andcolorpicker.util

import kotlin.reflect.KMutableProperty0

fun KMutableProperty0<Boolean>.marker(block: () -> Unit) {
  set(true)
  block.invoke()
  set(false)
}
