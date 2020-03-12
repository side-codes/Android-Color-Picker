package codes.side.andcolorpicker.model

import androidx.annotation.ColorInt

// TODO: Leave as a marker?
// TODO: Pull everything to factories/converters
interface ColorModel {
  @get:ColorInt
  val colorInt: Int

  fun setFromHSL(h: Float, s: Float, l: Float)

  fun setFromHSL(hsl: FloatArray)

  fun setFromColor(@ColorInt color: Int)

  fun setFromRGB(r: Int, g: Int, b: Int)
}
