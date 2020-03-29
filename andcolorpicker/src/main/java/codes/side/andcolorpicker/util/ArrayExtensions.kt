package codes.side.andcolorpicker.util

inline fun IntArray.mapToIntArray(
  destination: IntArray,
  transform: (Int) -> Int
): IntArray {
  forEachIndexed { index, t ->
    destination[index] = transform(t)
  }
  return destination
}
