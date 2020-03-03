package me.dummyco.andcolorpicker.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar.Mode

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    andColorPickerHView.addListener(
      object : HSLColorPickerSeekBar.OnColorPickListener {
        override fun onColorPicking(
          color: HSLColorPickerSeekBar.HSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
          colorizeTextView(color)
          notifySeekBarsOnHueChange(color)
        }

        override fun onColorPicked(
          color: HSLColorPickerSeekBar.HSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
          colorizeTextView(color)
          notifySeekBarsOnHueChange(color)
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
  }

  private fun notifySeekBarsOnHueChange(color: HSLColorPickerSeekBar.HSLColor) {
    andColorPickerSView.currentColor = color
    andColorPickerLView.currentColor = color
  }

  private fun colorizeTextView(color: HSLColorPickerSeekBar.HSLColor) {
    colorTextView.setBackgroundColor(color.clearColorInt)
    colorTextView.text = String.format(
      "#%06X",
      0xFFFFFF and color.clearColorInt
    )
  }
}
