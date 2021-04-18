package codes.side.andcolorpicker.app.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import codes.side.andcolorpicker.app.R
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.model.IntegerCMYKColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import codes.side.andcolorpicker.view.picker.OnIntegerCMYKColorPickListener
import kotlinx.android.synthetic.main.fragment_cmyk_seek_bar.*

class CMYKSeekBarFragment : Fragment(R.layout.fragment_cmyk_seek_bar) {
  companion object {
    private const val TAG = "CMYKSeekBarFragment"
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(
      view,
      savedInstanceState
    )

    val pickerGroup = PickerGroup<IntegerCMYKColor>().also {
      it.registerPickers(
        cyanCMYKColorPickerSeekBar,
        magentaCMYKColorPickerSeekBar,
        yellowCMYKColorPickerSeekBar,
        blackCMYKColorPickerSeekBar
      )
    }

    pickerGroup.addListener(
      object : OnIntegerCMYKColorPickListener() {
        override fun onColorChanged(
          picker: ColorSeekBar<IntegerCMYKColor>,
          color: IntegerCMYKColor,
          value: Int
        ) {
          swatchView.setSwatchColor(
            color
          )
        }
      }
    )

    pickerGroup.setColor(
      IntegerCMYKColor().also {
        it.intC = 70
        it.intM = 40
        it.intY = 20
        it.intK = 50
      }
    )
  }
}
