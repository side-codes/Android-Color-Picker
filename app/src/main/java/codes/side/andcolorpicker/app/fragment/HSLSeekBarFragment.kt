package codes.side.andcolorpicker.app.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import codes.side.andcolorpicker.app.ColorizationConsumer
import codes.side.andcolorpicker.app.R
import codes.side.andcolorpicker.app.util.firstIsInstanceOrNull
import codes.side.andcolorpicker.converter.*
import codes.side.andcolorpicker.dialogs.ColorPickerDialogFragment
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import com.google.android.material.button.MaterialButton
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.typeface.library.materialdesigndx.MaterialDesignDx
import com.mikepenz.iconics.utils.icon
import com.mikepenz.iconics.utils.padding
import com.mikepenz.iconics.utils.size
import kotlinx.android.synthetic.main.fragment_hsl_seek_bar.*

class HSLSeekBarFragment : Fragment(R.layout.fragment_hsl_seek_bar) {
  companion object {
    private const val TAG = "HSLSeekBarFragment"
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

    // API 19 backward-compatible way
    // Set it directly for newer versions:
    // android:background="@drawable/bg_transparency_pattern"
    // android:backgroundTint="@color/colorTransparencyTileTint"
    colorContainerFrameLayout.background =
      requireNotNull(
        ContextCompat.getDrawable(
          requireContext(),
          R.drawable.bg_transparency_pattern
        )
      )
        .mutate()
        .also {
          it.colorFilter = PorterDuffColorFilter(
            ContextCompat.getColor(
              requireContext(),
              R.color.colorTransparencyTileTint
            ),
            PorterDuff.Mode.SRC_ATOP
          )
        }

    colorfulViews.add(hueRadioButton)
    colorfulViews.add(saturationRadioButton)
    colorfulViews.add(lightnessRadioButton)
    colorfulViews.add(pureRadioButton)
    colorfulViews.add(outputRadioButton)
    colorfulViews.add(colorContainerFrameLayout)
    colorfulViews.add(colorTextView)
    colorfulViews.add(setRandomColorButton)
    colorfulViews.add(showDialogButton)

    pickerGroup.addListener(
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

    showDialogButton.setOnClickListener {
      ColorPickerDialogFragment().show(
        childFragmentManager,
        null
      )
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
    showDialogButton.icon =
      IconicsDrawable(requireContext()).icon(MaterialDesignDx.Icon.gmf_dynamic_feed)
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    colorizationConsumer = arrayOf(
      parentFragment,
      activity
    ).firstIsInstanceOrNull<ColorizationConsumer>()
  }

  private fun randomizePickedColor() {
    pickerGroup.setColor(
      IntegerHSLColor.createRandomColor().also {
        it.intL = 20 + it.intL / 2
      }
    )
  }

  @SuppressLint("SetTextI18n")
  private fun colorize(color: IntegerHSLColor) {
    val contrastColor = color.toContrastColor()
    val alphaContrastColor = color.toContrastColor(ContrastColorAlphaMode.LIGHT_BACKGROUND)
    colorizeHSLColorCache.setFrom(color)
    colorizeHSLColorCache.floatL = colorizeHSLColorCache.floatL.coerceAtMost(0.8f)

    val opaqueColorInt = colorizeHSLColorCache.toOpaqueColorInt()
    colorfulViews.forEach {
      when (it) {
        is MaterialButton -> {
          it.setTextColor(contrastColor)
          it.backgroundTintList = ColorStateList.valueOf(opaqueColorInt)
          it.iconTint = ColorStateList.valueOf(contrastColor)
        }
        is RadioButton -> {
          CompoundButtonCompat.setButtonTintList(
            it,
            ColorStateList.valueOf(opaqueColorInt)
          )
        }
        // Any other TextView is considered as raw color holder
        is TextView -> {
          it.setTextColor(alphaContrastColor)
          it.setBackgroundColor(color.toColorInt())
          TextViewCompat.setCompoundDrawableTintList(
            it,
            ColorStateList.valueOf(alphaContrastColor)
          )
        }
      }
    }

    colorTextView.text =
      "RGB: [${color.getRInt()} ${color.getGInt()} ${color.getBInt()}]\n" +
          "HEX: ${String.format(
            "#%06X",
            0xFFFFFF and color.toColorInt()
          )}\n" +
          "HSL: [${color.intH} ${color.intS} ${color.intL}]\n" +
          "Alpha: ${String.format(
            "%.2f",
            color.alpha
          )}"

    colorizationConsumer?.colorize(color)
  }
}
