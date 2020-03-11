package codes.side.andcolorpicker.model

import androidx.annotation.ColorInt

interface ColorModel {
  fun setFromHSL(h: Float, s: Float, l: Float)

  fun setFromHSL(hsl: FloatArray)

  fun setFromColor(@ColorInt color: Int)

  fun setFromRGB(r: Int, g: Int, b: Int)
}
