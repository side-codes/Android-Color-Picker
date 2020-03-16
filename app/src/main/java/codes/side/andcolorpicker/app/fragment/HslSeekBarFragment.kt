package codes.side.andcolorpicker.app.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import codes.side.andcolorpicker.app.ColorizationConsumer
import codes.side.andcolorpicker.app.R
import codes.side.andcolorpicker.app.util.firstIsInstanceOrNull
import codes.side.andcolorpicker.converter.*
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.ColorSeekBar
import com.google.android.material.button.MaterialButton
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.typeface.library.materialdesigndx.MaterialDesignDx
import com.mikepenz.iconics.utils.icon
import com.mikepenz.iconics.utils.padding
import com.mikepenz.iconics.utils.size
import kotlinx.android.synthetic.main.fragment_hsl_seekbar.*

class HslSeekBarFragment : Fragment(R.layout.fragment_hsl_seekbar) {
  companion object {
    private const val TAG = "HslSeekBarFragment"
    private const val colorTextViewIconSizeDp = 24
    private const val colorTextViewIconPaddingDp = 6
  }

  private val colorfulViews = hashSetOf<View>()
  private val pickerGroup =
    PickerGroup<IntegerHSLColor>()
  private var colorizationConsumer: ColorizationConsumer? = null
  private val colorizeHSLColorCache = IntegerHSLColor()

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

    hueColorPickerSeekBar.addListener(
      object : HSLColorPickerSeekBar.DefaultOnColorPickListener() {
        override fun onColorChanged(
          picker: ColorSeekBar<IntegerHSLColor>,
          color: IntegerHSLColor,
          value: Int
        ) {
          colorize(color)
        }
      }
    )

    pickerGroup.registerPicker(
      hueColorPickerSeekBar
    )

    pickerGroup.registerPickers(
      hueColorPickerSeekBar,
      saturationColorPickerSeekBar,
      lightnessColorPickerSeekBar,
      alphaColorPickerSeekBar,
      dynamicColorPickerSeekBar
    )

    randomizePickedColor()

    val radioColorModelsMap = hashMapOf(
      R.id.hueRadioButton to HSLColorPickerSeekBar.Mode.MODE_HUE,
      R.id.saturationRadioButton to HSLColorPickerSeekBar.Mode.MODE_SATURATION,
      R.id.lightnessRadioButton to HSLColorPickerSeekBar.Mode.MODE_LIGHTNESS
    )
    colorModelRadioGroup.setOnCheckedChangeListener { _, checkedId ->
      dynamicColorPickerSeekBar.mode = requireNotNull(radioColorModelsMap[checkedId])
    }

    val radioColoringModesMap = hashMapOf(
      R.id.pureRadioButton to HSLColorPickerSeekBar.ColoringMode.PURE_COLOR,
      R.id.outputRadioButton to HSLColorPickerSeekBar.ColoringMode.OUTPUT_COLOR
    )
    coloringModeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
      pickerGroup.forEach {
        (it as? HSLColorPickerSeekBar)?.coloringMode =
          requireNotNull(radioColoringModesMap[checkedId])
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
          .size { IconicsSize.dp(colorTextViewIconSizeDp + colorTextViewIconPaddingDp * 2) }
          .padding { IconicsSize.dp(colorTextViewIconPaddingDp) },
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
    ).firstIsInstanceOrNull<ColorizationConsumer>()
  }

  // TODO: Delegate to group?
  private fun randomizePickedColor() {
    pickerGroup.setColor(
      IntegerHSLColor.createRandomColor().also {
        it.intL = 20 + it.intL / 2
      }
    )
  }

  @SuppressLint("SetTextI18n")
  private fun colorize(color: IntegerHSLColor) {
    val contrastColor = color.getContrastColor()
    colorizeHSLColorCache.setFromHSLColor(color)
    colorizeHSLColorCache.floatL = colorizeHSLColorCache.floatL.coerceAtMost(0.8f)

    colorfulViews.forEach {
      when (it) {
        is MaterialButton -> {
          it.setTextColor(contrastColor)
          it.backgroundTintList = ColorStateList.valueOf(colorizeHSLColorCache.convertToColorInt())
          it.iconTint = ColorStateList.valueOf(contrastColor)
        }
        is RadioButton -> {
          it.buttonTintList = ColorStateList.valueOf(colorizeHSLColorCache.convertToColorInt())
        }
        // Any other TextView is considered as true color holder
        is TextView -> {
          it.setTextColor(contrastColor)
          it.setBackgroundColor(color.convertToColorInt())
          it.compoundDrawables.first().setTintList(ColorStateList.valueOf(contrastColor))
        }
      }
    }

    colorTextView.text =
      "RGB: [${color.getRInt()} ${color.getGInt()} ${color.getBInt()}]\n" +
          "HEX: ${String.format(
            "#%06X",
            0xFFFFFF and color.convertToColorInt()
          )}\n" +
          "HSL: $color"

    colorizationConsumer?.colorize(color)
  }
}
