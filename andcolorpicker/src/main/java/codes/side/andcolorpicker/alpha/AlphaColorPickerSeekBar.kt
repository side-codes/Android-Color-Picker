package codes.side.andcolorpicker.alpha

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.factory.ColorFactory
import codes.side.andcolorpicker.view.ColorSeekBar


// TODO: Make bg_transparency_pattern programmatic
// TODO: Think on making that non-abstract
// TODO: Think on adding AlphaColor layer
abstract class AlphaColorPickerSeekBar<C : Color> @JvmOverloads constructor(
  colorFactory: ColorFactory<C>,
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = androidx.appcompat.R.attr.seekBarStyle
) :
  ColorSeekBar<C>(
    colorFactory,
    context,
    attrs,
    defStyle
  ) {

  init {
    init()
  }

  private fun init() {
    setupBackground()
    setupThumb()
  }

  private fun setupBackground() {

  }

  override fun onSetupProgressDrawableLayers(layers: Array<Drawable>): Array<Drawable> {
    val options = BitmapFactory.Options().also {
      it.inMutable = true
    }
    val bitmap = BitmapFactory.decodeResource(
      resources,
      R.drawable.bg_transparency_tile_horizontal,
      options
    )

    val patternDrawable = BitmapDrawable(
      resources,
      bitmap
    ).also {
      it.setTileModeXY(
        Shader.TileMode.REPEAT,
        Shader.TileMode.CLAMP
      )
    }

    //val id = ScaleDrawable(
    //  patternDrawable,
    //  Gravity.FILL_HORIZONTAL,
    //  1f,
    //  0.5f
    //)

    // TODO: Handle rounded corners
    return arrayOf(
      patternDrawable
    )
  }

  private fun setupThumb() {

  }
}
