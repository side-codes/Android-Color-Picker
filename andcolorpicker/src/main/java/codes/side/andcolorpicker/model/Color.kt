package codes.side.andcolorpicker.model

// TODO: Make serializable / parcelable?
// TODO: Leave as a marker?
interface Color : Cloneable {

  val colorKey: ColorKey

  // [0, 1]
  val alpha: Float

  public override fun clone(): Any {
    return super.clone()
  }
}
