package codes.side.andcolorpicker.alpha

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import codes.side.andcolorpicker.ColorSeekBar
import codes.side.andcolorpicker.model.ColorModel

class AlphaColorPickerSeekBar : ColorSeekBar<ColorModel> {
  override var currentColor: ColorModel
    get() = TODO("Not yet implemented")
    set(value) {}

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : super(
    context,
    attrs
  ) {
    init()
  }

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(
    context,
    attrs,
    defStyleAttr
  ) {
    init()
  }

  private fun init() {

  }

  override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

  }
}
