package me.dummyco.andcolorpicker.app

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigndx.MaterialDesignDx
import com.mikepenz.iconics.utils.icon
import kotlinx.android.synthetic.main.activity_main.*
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar.Mode
import me.dummyco.andcolorpicker.PickerGroup
import me.dummyco.andcolorpicker.model.DiscreteHSLColor
import me.dummyco.andcolorpicker.model.DiscreteHSLColor.Companion.createRandomColor
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

  private val colorfulViews = hashSetOf<View>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    dynamicSwitchButton.icon = IconicsDrawable(this).icon(MaterialDesignDx.Icon.gmf_loop)
    setRandomColorButton.icon = IconicsDrawable(this).icon(MaterialDesignDx.Icon.gmf_invert_colors)

    colorfulViews.add(dynamicSwitchButton)
    colorfulViews.add(setRandomColorButton)
    colorfulViews.add(colorTextView)

    andColorPickerHView.addListener(
      // TODO: Add default listener
      object : HSLColorPickerSeekBar.OnColorPickListener {
        override fun onColorPicking(
          picker: HSLColorPickerSeekBar,
          color: DiscreteHSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
        }

        override fun onColorPicked(
          picker: HSLColorPickerSeekBar,
          color: DiscreteHSLColor,
          mode: Mode,
          value: Int,
          fromUser: Boolean
        ) {
        }

        override fun onColorChanged(
          picker: HSLColorPickerSeekBar,
          color: DiscreteHSLColor,
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

    randomizePickedColor()

    setRandomColorButton.setOnClickListener {
      randomizePickedColor()
    }

    dynamicSwitchButton.setOnClickListener {
      andColorPickerDynamicView.mode =
        AVAILABLE_SWITCH_MODES[(AVAILABLE_SWITCH_MODES.indexOf(andColorPickerDynamicView.mode) + 1) % AVAILABLE_SWITCH_MODES.size]
    }
  }

  private fun randomizePickedColor() {
    andColorPickerHView.currentColor = createRandomColor().also {
      it.intL = it.intL / 2
    }
  }

  @SuppressLint("SetTextI18n")
  private fun colorize(color: DiscreteHSLColor) {
    appBarLayout.backgroundTintList = ColorStateList.valueOf(color.colorInt)

    val statusBarColor = color.copy().also {
      it.intL += PRIMARY_DARK_LIGHTNESS_SHIFT
    }
    window.statusBarColor = statusBarColor.colorInt

    val red: Int = Color.red(color.colorInt)
    val green: Int = Color.green(color.colorInt)
    val blue: Int = Color.blue(color.colorInt)

    val textColor =
      if (red * 0.299f + green * 0.587f + blue * 0.114f > 186) Color.BLACK else Color.WHITE

    colorfulViews.forEach {
      it.setBackgroundColor(color.colorInt)
      if (it is TextView) {
        it.setTextColor(textColor)
      }
      if (it is MaterialButton) {
        it.iconTint = ColorStateList.valueOf(textColor)
      }
    }

    toolbar.setTitleTextColor(textColor)
    toolbar.setSubtitleTextColor(textColor)

    colorTextView.text =
      "RGB: [$red $green $blue]\n" +
          "HEX: ${String.format(
            "#%06X",
            0xFFFFFF and color.colorInt
          )}\n" +
          "HSL: $color"
  }
}
