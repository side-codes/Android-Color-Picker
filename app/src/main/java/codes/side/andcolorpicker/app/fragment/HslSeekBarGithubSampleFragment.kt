package codes.side.andcolorpicker.app.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import codes.side.andcolorpicker.DefaultOnColorPickListener
import codes.side.andcolorpicker.HSLColorPickerSeekBar
import codes.side.andcolorpicker.HSLColorPickerSeekBar.ColoringMode
import codes.side.andcolorpicker.HSLColorPickerSeekBar.Mode
import codes.side.andcolorpicker.PickerGroup
import codes.side.andcolorpicker.app.R
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.registerPickers
import kotlinx.android.synthetic.main.fragment_hsl_seekbar_github_sample.*

class HslSeekBarGithubSampleFragment : Fragment(R.layout.fragment_hsl_seekbar_github_sample) {
  companion object {
    private const val TAG = "HslSeekBarGithubSampleFragment"
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(
      view,
      savedInstanceState
    )

    // Configure picker color model programmatically
    hueColorPickerSeekBar.mode = Mode.MODE_HUE // Mode.MODE_SATURATION, Mode.MODE_LIGHTNESS

    // Configure coloring mode programmatically
    hueColorPickerSeekBar.coloringMode = ColoringMode.PURE_COLOR // ColoringMode.OUTPUT_COLOR

    // Group pickers with PickerGroup to automatically synchronize color across them
    val pickerGroup = PickerGroup().also {
      it.registerPickers(
        hueColorPickerSeekBar,
        saturationColorPickerSeekBar,
        lightnessColorPickerSeekBar
      )
    }

    // Set desired color programmatically
    pickerGroup.setColor(
      IntegerHSLColor().also {
        it.setFromColor(
          Color.rgb(
            28,
            84,
            187
          )
        )
      }
    )

    // Set color components programmatically
    hueColorPickerSeekBar.progress = 50

    // Get current color immediatly
    Log.d(
      TAG,
      "Current color is ${hueColorPickerSeekBar.currentColor}"
    )

    // Listen for changes
    hueColorPickerSeekBar.addListener(
      object : DefaultOnColorPickListener() {
        override fun onColorChanged(
          picker: HSLColorPickerSeekBar,
          color: IntegerHSLColor,
          mode: Mode,
          value: Int
        ) {
          Log.d(
            TAG,
            "$color picked"
          )
        }
      }
    )
  }
}
