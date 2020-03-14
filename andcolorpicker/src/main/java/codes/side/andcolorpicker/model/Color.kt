package codes.side.andcolorpicker.model

import androidx.annotation.ColorInt
import codes.side.andcolorpicker.converter.BoundColorConverter
import codes.side.andcolorpicker.converter.ColorConverter

// TODO: Make serializable / parcelable?
// TODO: Leave as a marker?
// TODO: Pull everything to factories/converters
interface Color<C : Color<C>> : Cloneable {
  val converter: ColorConverter<C>

  // Probably providing more optimizations?
  val localConverter: BoundColorConverter<C>

  val alpha: Float

  @Deprecated("Will be moved to converter layer")
  fun setFromHSL(h: Float, s: Float, l: Float)

  @Deprecated("Will be moved to converter layer")
  fun setFromHSL(hsl: FloatArray)

  @Deprecated("Will be moved to converter layer")
  fun setFromColor(@ColorInt color: Int)

  @Deprecated("Will be moved to converter layer")
  fun setFromRGB(r: Int, g: Int, b: Int)

  public override fun clone(): Color<C>
}
