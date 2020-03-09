package me.dummyco.andcolorpicker.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigndx.MaterialDesignDx
import com.mikepenz.iconics.utils.icon
import kotlinx.android.synthetic.main.fragment_hsl_seekbar.*
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar
import me.dummyco.andcolorpicker.PickerGroup
import me.dummyco.andcolorpicker.model.DiscreteHSLColor
import me.dummyco.andcolorpicker.registerPickers

class HslSeekBarFragment : Fragment(R.layout.fragment_hsl_seekbar) {
  companion object {
    private val AVAILABLE_SWITCH_MODES = listOf(
      HSLColorPickerSeekBar.Mode.MODE_HUE,
      HSLColorPickerSeekBar.Mode.MODE_SATURATION,
      HSLColorPickerSeekBar.Mode.MODE_LIGHTNESS
    )
    private val AVAILABLE_COLORING_MODES = listOf(
      HSLColorPickerSeekBar.ColoringMode.PURE_COLOR,
      HSLColorPickerSeekBar.ColoringMode.OUTPUT_COLOR
    )
  }

  private val colorfulViews = hashSetOf<View>()
  private val pickerGroup = PickerGroup()
  private var colorizationConsumer: ColorizationConsumer? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(
      view,
      savedInstanceState
    )

    dynamicSwitchButton.icon =
      IconicsDrawable(requireContext()).icon(MaterialDesignDx.Icon.gmf_loop)
    setRandomColorButton.icon =
      IconicsDrawable(requireContext()).icon(MaterialDesignDx.Icon.gmf_invert_colors)
    coloringModeSwitchButton.icon =
      IconicsDrawable(requireContext()).icon(MaterialDesignDx.Icon.gmf_color_lens)

    colorfulViews.add(dynamicSwitchButton)
    colorfulViews.add(setRandomColorButton)
    colorfulViews.add(coloringModeSwitchButton)
    colorfulViews.add(colorTextView)


    andColorPickerHView.addListener(
      object : HSLColorPickerSeekBar.DefaultOnColorPickListener() {
        override fun onColorChanged(
          picker: HSLColorPickerSeekBar,
          color: DiscreteHSLColor,
          mode: HSLColorPickerSeekBar.Mode,
          value: Int
        ) {
          colorize(color)
        }
      }
    )

    val pickers = arrayListOf(
      andColorPickerHView,
      andColorPickerSView,
      andColorPickerLView,
      andColorPickerDynamicView
    )

    pickerGroup.registerPickers(pickers)

    randomizePickedColor()

    setRandomColorButton.setOnClickListener {
      randomizePickedColor()
    }

    dynamicSwitchButton.setOnClickListener {
      val newMode =
        AVAILABLE_SWITCH_MODES[(AVAILABLE_SWITCH_MODES.indexOf(andColorPickerDynamicView.mode) + 1) % AVAILABLE_SWITCH_MODES.size]
      andColorPickerDynamicView.mode = newMode
    }

    coloringModeSwitchButton.setOnClickListener {
      val newMode =
        AVAILABLE_COLORING_MODES[(AVAILABLE_COLORING_MODES.indexOf(andColorPickerHView.coloringMode) + 1) % AVAILABLE_COLORING_MODES.size]

      pickers.forEach {
        it.coloringMode = newMode
      }
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    colorizationConsumer = arrayOf(
      parentFragment,
      activity
    ).firstIsInstance<ColorizationConsumer>()
  }

  private fun randomizePickedColor() {
    andColorPickerHView.currentColor = DiscreteHSLColor.createRandomColor().also {
      it.intL = 20 + it.intL / 2
    }
  }

  @SuppressLint("SetTextI18n")
  private fun colorize(color: DiscreteHSLColor) {
    val contrastColor = color.createContrastColor()

    colorfulViews.forEach {
      it.setBackgroundColor(color.colorInt)
      if (it is TextView) {
        it.setTextColor(contrastColor)
      }
      if (it is MaterialButton) {
        it.iconTint = ColorStateList.valueOf(contrastColor)
      }
    }

    colorTextView.text =
      "RGB: [${color.rInt} ${color.gInt} ${color.bInt}]\n" +
          "HEX: ${String.format(
            "#%06X",
            0xFFFFFF and color.colorInt
          )}\n" +
          "HSL: $color"

    colorizationConsumer?.colorize(color)
  }
}
