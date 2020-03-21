package codes.side.andcolorpicker.view.swatch

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.converter.ColorConverterHub
import codes.side.andcolorpicker.model.Color

class SwatchView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(
  context,
  attrs,
  defStyleAttr
) {
  private val patternDrawable =
    requireNotNull(context.getDrawable(R.drawable.bg_transparency_pattern))

  init {
    background = LayerDrawable(
      arrayOf(
        patternDrawable,
        ColorDrawable()
      )
    )
  }

  fun setSwatchColor(color: Color) {
    ((background as LayerDrawable).getDrawable(1) as ColorDrawable).color =
      ColorConverterHub.getConverterByKey(color.colorKey).convertToColorInt(color)
  }

  fun setSwatchPatternTint(@ColorInt tintColor: Int) {
    patternDrawable.setTint(tintColor)
  }
}
