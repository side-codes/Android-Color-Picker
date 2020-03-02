package me.dummyco.andcolorpicker.app

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.dummyco.andcolorpicker.AndColorPickerSeekBar
import me.dummyco.andcolorpicker.AndColorPickerSeekBar.Mode

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    andColorPickerHView.colorPickListener = object : AndColorPickerSeekBar.OnColorPickListener {
      override fun onColorPicking(
        color: Int,
        mode: Mode,
        value: Int,
        fromUser: Boolean
      ) {
        colorizeTextView(color)
        notifySeekBarsOnHueChange(color)
      }

      override fun onColorPicked(
        color: Int,
        mode: Mode,
        value: Int,
        fromUser: Boolean
      ) {
        colorizeTextView(color)
        notifySeekBarsOnHueChange(color)
      }
    }

    andColorPickerSView.mode = Mode.MODE_SATURATION
    andColorPickerLView.mode = Mode.MODE_LIGHTNESS
    // TODO: Encapsulate
    andColorPickerSView.progress = 100
    andColorPickerLView.progress = 50

    setColorButton.setOnClickListener {
      andColorPickerHView.currentColor = Color.rgb(
        48,
        85,
        56
      )
    }
  }

  private fun notifySeekBarsOnHueChange(color: Int) {
    andColorPickerSView.currentColor = color
    andColorPickerLView.currentColor = color
  }

  private fun colorizeTextView(color: Int) {
    colorTextView.setBackgroundColor(color)
    colorTextView.text = String.format(
      "#%06X",
      0xFFFFFF and color
    )
  }
}
