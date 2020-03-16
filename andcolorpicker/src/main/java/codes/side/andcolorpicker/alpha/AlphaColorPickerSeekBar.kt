package codes.side.andcolorpicker.alpha

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import codes.side.andcolorpicker.ColorSeekBar
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.factory.ColorFactory

// TODO: Make bg_transparency_pattern programmatic
// TODO: Think on making that non-abstract
// TODO: Think on adding AlphaColor layer
abstract class AlphaColorPickerSeekBar<C : Color> :
  ColorSeekBar<C> {
  constructor(colorFactory: ColorFactory<C>, context: Context) : super(
    colorFactory,
    context
  ) {
    init()
  }

  constructor(
    colorFactory: ColorFactory<C>,
    context: Context,
    attrs: AttributeSet?
  ) : super(
    colorFactory,
    context,
    attrs
  ) {
    init()
  }

  constructor(
    colorFactory: ColorFactory<C>,
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(
    colorFactory,
    context,
    attrs,
    defStyleAttr
  ) {
    init()
  }

  private fun init() {
    setupBackground()
    setupProgressDrawable()
    setupThumb()
  }

  private fun setupBackground() {

  }

  private fun setupProgressDrawable() {
    val backgroundPaddingPx = resources.getDimensionPixelOffset(R.dimen.acp_seek_background_padding)

    // TODO: Handle rounded corners
    progressDrawable = LayerDrawable(
      arrayOf(
        resources.getDrawable(
          R.drawable.bg_transparency_pattern,
          context.theme
        )
      )
    ).also {
      it.setLayerInset(
        0,
        backgroundPaddingPx,
        backgroundPaddingPx,
        backgroundPaddingPx,
        backgroundPaddingPx
      )
    }
  }

  private fun setupThumb() {

  }
}
