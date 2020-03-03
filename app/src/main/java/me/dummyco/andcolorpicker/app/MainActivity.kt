package me.dummyco.andcolorpicker.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar.Mode
import me.dummyco.andcolorpicker.PickerGroup
import me.dummyco.andcolorpicker.registerPickers

class MainActivity : AppCompatActivity() {

  companion object {
    private const val TAG = "MainActivity"
  }

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
        }

        override fun onColorPicked(
          picker: HSLColorPickerSeekBar,
          color: HSLColorPickerSeekBar.HSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
        }

        override fun onColorChanged(
          picker: HSLColorPickerSeekBar,
          color: HSLColorPickerSeekBar.HSLColor,
          mode: Mode,
          value: Int
        ) {
          colorizeTextView(picker)
        }
      }
    )

    andColorPickerSView.mode = Mode.MODE_SATURATION
    andColorPickerLView.mode = Mode.MODE_LIGHTNESS

    val group = PickerGroup()
    group.registerPickers(
      andColorPickerHView,
      andColorPickerSView,
      andColorPickerLView,
      andColorPickerDynamicView.also {
        it.tag = "Dynamic"
      }
    )

    // TODO: Encapsulate
    andColorPickerSView.progress = 100
    andColorPickerLView.progress = 50

    setColorButton.setOnClickListener {
      andColorPickerHView.currentColor = HSLColorPickerSeekBar.HSLColor().setFromHSL(
        floatArrayOf(
          132.97296f,
          0.2781955f,
          0.26078433f
        )
      )
    }

    dynamicSwitchButton.setOnClickListener {
      val availableModes = listOf(
        Mode.MODE_HUE,
        Mode.MODE_SATURATION,
        Mode.MODE_LIGHTNESS
      )
      andColorPickerDynamicView.mode =
        availableModes[(availableModes.indexOf(andColorPickerDynamicView.mode) + 1) % availableModes.size]
    }
  }

  @SuppressLint("SetTextI18n")
  private fun colorizeTextView(picker: HSLColorPickerSeekBar) {
    val color = picker.currentColor
    Log.d(
      TAG,
      "mode = ${picker.mode}, color = $color"
    )
    colorTextView.setBackgroundColor(color.colorInt)
    colorTextView.text = String.format(
      "#%06X",
      0xFFFFFF and color.colorInt
    )

    hlsTextView.text =
      "[${color.h}, ${color.s}, ${color.l}]"
  }
}
