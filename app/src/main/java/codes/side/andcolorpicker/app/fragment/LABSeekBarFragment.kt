package codes.side.andcolorpicker.app.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import codes.side.andcolorpicker.app.R
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.model.IntegerLABColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import kotlinx.android.synthetic.main.fragment_cmyk_seek_bar.swatchView
import kotlinx.android.synthetic.main.fragment_lab_seek_bar.*

class LABSeekBarFragment : Fragment(R.layout.fragment_lab_seek_bar) {
  companion object {
    private const val TAG = "LABSeekBarFragment"
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(
      view,
      savedInstanceState
    )

    val pickerGroup = PickerGroup<IntegerLABColor>().also {
      it.registerPickers(
        lLABColorPickerSeekBar,
        aLABColorPickerSeekBar,
        bLABColorPickerSeekBar
      )
    }

    pickerGroup.addListener(
      object :
        ColorSeekBar.DefaultOnColorPickListener<ColorSeekBar<IntegerLABColor>, IntegerLABColor>() {
        override fun onColorChanged(
          picker: ColorSeekBar<IntegerLABColor>,
          color: IntegerLABColor,
          value: Int
        ) {
          swatchView.setSwatchColor(
            color
          )
        }
      }
    )

    pickerGroup.setColor(
      IntegerLABColor().also {
        it.intL = 50
        it.intA = 0
        it.intB = 0
      }
    )
  }
}
