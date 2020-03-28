package codes.side.andcolorpicker.util

public inline fun <T> Iterable<T>.mapToIntArray(
  destination: IntArray,
  transform: (T) -> Int
): IntArray {
  forEachIndexed { index, t ->
    destination[index] = transform(t)
  }
  return destination
}
