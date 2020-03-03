package me.dummyco.andcolorpicker.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar.Mode
import me.dummyco.andcolorpicker.PickerGroup
import me.dummyco.andcolorpicker.registerPickers

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    andColorPickerHView.addListener(
      // TODO: Add default listener
      object : HSLColorPickerSeekBar.OnColorPickListener {
        override fun onColorPicking(
          picker: HSLColorPickerSeekBar,
          color: HSLColorPickerSeekBar.HSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
          colorizeTextView(color)
        }

        override fun onColorPicked(
          picker: HSLColorPickerSeekBar,
          color: HSLColorPickerSeekBar.HSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
          colorizeTextView(color)
        }
      }
    )

    andColorPickerSView.mode = Mode.MODE_SATURATION
    andColorPickerLView.mode = Mode.MODE_LIGHTNESS
    // TODO: Encapsulate
    andColorPickerSView.progress = 100
    andColorPickerLView.progress = 50

    setColorButton.setOnClickListener {
      andColorPickerHView.currentColor = HSLColorPickerSeekBar.HSLColor().setFromRGB(
        48,
        85,
        56
      )
    }

    val group = PickerGroup()
    group.registerPickers(
      andColorPickerHView,
      andColorPickerSView,
      andColorPickerLView
    )
  }

  private fun colorizeTextView(color: HSLColorPickerSeekBar.HSLColor) {
    colorTextView.setBackgroundColor(color.clearColorInt)
    colorTextView.text = String.format(
      "#%06X",
      0xFFFFFF and color.clearColorInt
    )
  }
}
