package codes.side.andcolorpicker.model

import androidx.annotation.ColorInt

// TODO: Make serializable / parcelable?
// TODO: Leave as a marker?
// TODO: Pull everything to factories/converters
interface ColorModel : Cloneable {
  @Deprecated("Will be moved to converter layer")
  @get:ColorInt
  val colorInt: Int

  @Deprecated("Will be moved to converter layer")
  fun setFromHSL(h: Float, s: Float, l: Float)

  @Deprecated("Will be moved to converter layer")
  fun setFromHSL(hsl: FloatArray)

  @Deprecated("Will be moved to converter layer")
  fun setFromColor(@ColorInt color: Int)

  @Deprecated("Will be moved to converter layer")
  fun setFromRGB(r: Int, g: Int, b: Int)

  public override fun clone(): ColorModel
}
