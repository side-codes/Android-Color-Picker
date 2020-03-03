package me.dummyco.andcolorpicker.app

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar.Mode
import me.dummyco.andcolorpicker.PickerGroup
import me.dummyco.andcolorpicker.model.HSLColor
import me.dummyco.andcolorpicker.model.HSLColor.Companion.createRandomColor
import me.dummyco.andcolorpicker.registerPickers

class MainActivity : AppCompatActivity() {

  companion object {
    private const val TAG = "MainActivity"
    private const val PRIMARY_DARK_LIGHTNESS_SHIFT = -10
    private val AVAILABLE_SWITCH_MODES = listOf(
      Mode.MODE_HUE,
      Mode.MODE_SATURATION,
      Mode.MODE_LIGHTNESS
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    andColorPickerHView.addListener(
      // TODO: Add default listener
      object : HSLColorPickerSeekBar.OnColorPickListener {
        override fun onColorPicking(
          picker: HSLColorPickerSeekBar,
          color: HSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
        }

        override fun onColorPicked(
          picker: HSLColorPickerSeekBar,
          color: HSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
        }

        override fun onColorChanged(
          picker: HSLColorPickerSeekBar,
          color: HSLColor,
          mode: Mode,
          value: Int
        ) {
          colorize(color)
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
      andColorPickerDynamicView
    )

    andColorPickerHView.currentColor = createRandomColor()

    setColorButton.setOnClickListener {
      andColorPickerHView.currentColor = createRandomColor()
    }

    dynamicSwitchButton.setOnClickListener {
      andColorPickerDynamicView.mode =
        AVAILABLE_SWITCH_MODES[(AVAILABLE_SWITCH_MODES.indexOf(andColorPickerDynamicView.mode) + 1) % AVAILABLE_SWITCH_MODES.size]
    }
  }

  @SuppressLint("SetTextI18n")
  private fun colorize(color: HSLColor) {
    supportActionBar?.setBackgroundDrawable(GradientDrawable().also {
      it.color = ColorStateList.valueOf(color.colorInt)
    })

    val statusBarColor = color.copy().also {
      it.l += PRIMARY_DARK_LIGHTNESS_SHIFT
    }
    window.statusBarColor = statusBarColor.colorInt

    colorTextView.setBackgroundColor(color.colorInt)
    colorTextView.text = String.format(
      "#%06X",
      0xFFFFFF and color.colorInt
    )

    hlsTextView.text = color.toString()
  }
}
