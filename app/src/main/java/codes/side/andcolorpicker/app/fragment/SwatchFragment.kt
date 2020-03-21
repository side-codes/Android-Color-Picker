package codes.side.andcolorpicker.app.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import codes.side.andcolorpicker.app.R
import codes.side.andcolorpicker.converter.setFromColorInt
import codes.side.andcolorpicker.model.IntegerHSLColor
import kotlinx.android.synthetic.main.fragment_swatch.*

class SwatchFragment : Fragment(R.layout.fragment_swatch) {
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(
      view,
      savedInstanceState
    )

    swatchView.setSwatchPatternTint(
      ContextCompat.getColor(
        requireContext(),
        R.color.colorTransparencyTileTint
      )
    )

    swatchView.setSwatchColor(
      IntegerHSLColor().also {
        it.setFromColorInt(
          ColorUtils.setAlphaComponent(
            Color.MAGENTA,
            128
          )
        )
      }
    )
  }
}
