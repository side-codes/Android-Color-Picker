package codes.side.andcolorpicker.view.set

import android.content.Context
import android.util.AttributeSet
import codes.side.andcolorpicker.model.IntegerRGBColor
import codes.side.andcolorpicker.rgb.RGBColorPickerSeekBar

class RGBColorPickerSeekBarSet @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : ColorPickerSeekBarSet<IntegerRGBColor>(
  context,
  attrs,
  defStyle
) {

  init {
    RGBColorPickerSeekBar.Mode.values().forEach { rgbMode ->
      addLabel(rgbMode.nameStringResId)
      val picker =
        RGBColorPickerSeekBar(
          context
        ).also {
          it.mode = rgbMode
        }
      pickerGroup.registerPicker(picker)
      addView(picker)
    }
  }
}
