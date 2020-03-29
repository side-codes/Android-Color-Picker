package codes.side.andcolorpicker.app.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import codes.side.andcolorpicker.app.R
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.model.IntegerRGBColor
import codes.side.andcolorpicker.rgb.RGBColorPickerSeekBar
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import kotlinx.android.synthetic.main.fragment_rgb_seek_bar.*

class RGBSeekBarFragment : Fragment(R.layout.fragment_rgb_seek_bar) {

  companion object {
    private const val TAG = "RGBSeekBarFragment"
  }

  private val pickerGroup = PickerGroup<IntegerRGBColor>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(
      view,
      savedInstanceState
    )

    pickerGroup.registerPickers(
      redRGBColorPickerSeekBar,
      greenRGBColorPickerSeekBar,
      blueRGBColorPickerSeekBar
    )

    pickerGroup.addListener(
      object :
        ColorSeekBar.DefaultOnColorPickListener<ColorSeekBar<IntegerRGBColor>, IntegerRGBColor>() {
        override fun onColorChanged(
          picker: ColorSeekBar<IntegerRGBColor>,
          color: IntegerRGBColor,
          value: Int
        ) {
          swatchView.setSwatchColor(
            color
          )
        }
      }
    )

    pickerGroup.setColor(
      IntegerRGBColor().also {
        it.intR = 30
        it.intG = 130
        it.intB = 230
      }
    )

    val radioColoringModesMap = hashMapOf(
      R.id.pureRadioButton to RGBColorPickerSeekBar.ColoringMode.PURE_COLOR,
      R.id.plainRadioButton to RGBColorPickerSeekBar.ColoringMode.PLAIN_COLOR
    )
    coloringModeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
      pickerGroup.forEach {
        (it as? RGBColorPickerSeekBar)?.coloringMode =
          requireNotNull(radioColoringModesMap[checkedId])
      }
    }
  }
}
