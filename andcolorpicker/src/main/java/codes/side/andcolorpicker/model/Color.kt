package codes.side.andcolorpicker.model

import androidx.annotation.ColorInt

// TODO: Make serializable / parcelable?
// TODO: Leave as a marker?
// TODO: Pull everything to factories/converters
interface Color : Cloneable {
  val colorKey: ColorKey

  // [0, 1]
  val alpha: Float

  @Deprecated("Will be moved to converter layer")
  fun setFromHSL(h: Float, s: Float, l: Float)

  @Deprecated("Will be moved to converter layer")
  fun setFromHSL(hsl: FloatArray)

  @Deprecated("Will be moved to converter layer")
  fun setFromColor(@ColorInt color: Int)

  @Deprecated("Will be moved to converter layer")
  fun setFromRGB(r: Int, g: Int, b: Int)

  public override fun clone(): Any {
    return super.clone()
  }
}
