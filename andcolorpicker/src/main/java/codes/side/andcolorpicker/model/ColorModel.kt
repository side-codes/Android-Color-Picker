package codes.side.andcolorpicker.model

import androidx.annotation.ColorInt

// TODO: Make serializable / parcelable?
// TODO: Leave as a marker?
// TODO: Pull everything to factories/converters
interface ColorModel : Cloneable {
  @get:ColorInt
  val colorInt: Int

  fun setFromHSL(h: Float, s: Float, l: Float)

  fun setFromHSL(hsl: FloatArray)

  fun setFromColor(@ColorInt color: Int)

  fun setFromRGB(r: Int, g: Int, b: Int)

  public override fun clone(): ColorModel
}
