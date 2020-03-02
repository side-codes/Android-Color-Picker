package me.dummyco.andcolorpicker.app

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.dummyco.andcolorpicker.AndColorPickerSeekBar

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super
        .onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    andColorPickerHView
        .colorPickListener = object : AndColorPickerSeekBar.OnColorPickListener {
      override fun onColorPicking(color: Int, fromUser: Boolean) {
        colorizeTextView(color)
      }

      override fun onColorPicked(color: Int, fromUser: Boolean) {
        colorizeTextView(color)
      }
    }

    setColorButton
        .setOnClickListener {
          andColorPickerHView
              .currentColor = Color
              .MAGENTA
        }
  }

  private fun colorizeTextView(color: Int) {
    colorTextView
        .setBackgroundColor(color)
    colorTextView
        .text = String
        .format("#%06X", 0xFFFFFF and color)
  }
}
