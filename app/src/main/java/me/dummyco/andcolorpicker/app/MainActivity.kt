package me.dummyco.andcolorpicker.app

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.andColorPickerHView
import kotlinx.android.synthetic.main.activity_main.andColorPickerSView
import kotlinx.android.synthetic.main.activity_main.colorTextView
import kotlinx.android.synthetic.main.activity_main.setColorButton
import me.dummyco.andcolorpicker.AndColorPickerSeekBar
import me.dummyco.andcolorpicker.AndColorPickerSeekBar.Mode

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    andColorPickerHView.colorPickListener = object : AndColorPickerSeekBar.OnColorPickListener {
      override fun onColorPicking(
          color: Int,
          fromUser: Boolean
      ) {
        colorizeTextView(color)
        refreshSaturationSeekBar(color)
      }

      override fun onColorPicked(
          color: Int,
          fromUser: Boolean
      ) {
        colorizeTextView(color)
        refreshSaturationSeekBar(color)
      }
    }

    andColorPickerSView.mode = Mode.MODE_SATURATION

    setColorButton.setOnClickListener {
      andColorPickerHView.currentColor = Color.MAGENTA
    }
  }

  private fun refreshSaturationSeekBar(color: Int) {
    andColorPickerSView.currentColor = color
  }

  private fun colorizeTextView(color: Int) {
    colorTextView.setBackgroundColor(color)
    colorTextView.text = String.format(
        "#%06X",
        0xFFFFFF and color
    )
  }
}
