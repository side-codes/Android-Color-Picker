package me.dummyco.andcolorpicker.app.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.typeface.library.materialdesigndx.MaterialDesignDx
import com.mikepenz.iconics.utils.icon
import com.mikepenz.iconics.utils.padding
import com.mikepenz.iconics.utils.size
import kotlinx.android.synthetic.main.fragment_hsl_seekbar.*
import me.dummyco.andcolorpicker.HSLColorPickerSeekBar
import me.dummyco.andcolorpicker.PickerGroup
import me.dummyco.andcolorpicker.app.ColorizationConsumer
import me.dummyco.andcolorpicker.app.R
import me.dummyco.andcolorpicker.app.util.createContrastColor
import me.dummyco.andcolorpicker.app.util.firstIsInstance
import me.dummyco.andcolorpicker.model.DiscreteHSLColor
import me.dummyco.andcolorpicker.registerPickers

class HslSeekBarFragment : Fragment(R.layout.fragment_hsl_seekbar) {
  companion object {
    private const val TAG = "HslSeekBarFragment"
  }

  private val colorfulViews = hashSetOf<View>()
  private val pickerGroup = PickerGroup()
  private var colorizationConsumer: ColorizationConsumer? = null
  private val colorizeHSLColorCache = DiscreteHSLColor()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(
      view,
      savedInstanceState
    )

    setupIcons()

    colorfulViews.add(hueRadioButton)
    colorfulViews.add(saturationRadioButton)
    colorfulViews.add(lightnessRadioButton)
    colorfulViews.add(pureRadioButton)
    colorfulViews.add(outputRadioButton)
    colorfulViews.add(setRandomColorButton)
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

    val radioColorModelsMap = hashMapOf(
      R.id.hueRadioButton to HSLColorPickerSeekBar.Mode.MODE_HUE,
      R.id.saturationRadioButton to HSLColorPickerSeekBar.Mode.MODE_SATURATION,
      R.id.lightnessRadioButton to HSLColorPickerSeekBar.Mode.MODE_LIGHTNESS
    )
    colorModelRadioGroup.setOnCheckedChangeListener { _, checkedId ->
      andColorPickerDynamicView.mode = requireNotNull(radioColorModelsMap[checkedId])
    }

    val radioColoringModesMap = hashMapOf(
      R.id.pureRadioButton to HSLColorPickerSeekBar.ColoringMode.PURE_COLOR,
      R.id.outputRadioButton to HSLColorPickerSeekBar.ColoringMode.OUTPUT_COLOR
    )
    coloringModeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
      pickers.forEach {
        it.coloringMode = requireNotNull(radioColoringModesMap[checkedId])
      }
    }

    setRandomColorButton.setOnClickListener {
      randomizePickedColor()
    }
  }

  private fun setupIcons() {
    colorTextView
      .setCompoundDrawables(
        IconicsDrawable(requireContext()).icon(MaterialDesignDx.Icon.gmf_colorize)
          .size { IconicsSize.dp(24 + 6 * 2) }
          .padding { IconicsSize.dp(6) },
        null,
        null,
        null
      )
    setRandomColorButton.icon =
      IconicsDrawable(requireContext()).icon(MaterialDesignDx.Icon.gmf_invert_colors)
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    colorizationConsumer = arrayOf(
      parentFragment,
      activity
    ).firstIsInstance<ColorizationConsumer>()
  }

  // TODO: Delegate to group?
  private fun randomizePickedColor() {
    andColorPickerHView.currentColor = DiscreteHSLColor.createRandomColor().also {
      it.intL = 20 + it.intL / 2
    }
  }

  @SuppressLint("SetTextI18n")
  private fun colorize(color: DiscreteHSLColor) {
    val contrastColor = color.createContrastColor()
    colorizeHSLColorCache.setFromHSLColor(color)
    colorizeHSLColorCache.floatL = colorizeHSLColorCache.floatL.coerceAtMost(0.8f)

    colorfulViews.forEach {
      when (it) {
        is MaterialButton -> {
          it.setTextColor(contrastColor)
          it.backgroundTintList = ColorStateList.valueOf(colorizeHSLColorCache.colorInt)
          it.iconTint = ColorStateList.valueOf(contrastColor)
        }
        is RadioButton -> {
          it.buttonTintList = ColorStateList.valueOf(colorizeHSLColorCache.colorInt)
        }
        // Any other TextView is considered as true color holder
        is TextView -> {
          it.setTextColor(contrastColor)
          it.setBackgroundColor(color.colorInt)
          it.compoundDrawables.first().setTintList(ColorStateList.valueOf(contrastColor))
        }
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
