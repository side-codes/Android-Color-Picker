package codes.side.andcolorpicker.alpha

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.factory.ColorFactory
import codes.side.andcolorpicker.view.ColorSeekBar

// TODO: Minimize resource reads
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
  // TODO: Handle rounded corners
  override fun onSetupProgressDrawableLayers(layers: Array<Drawable>): Array<Drawable> {
    val layerList = layers.toMutableList()

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

    layerList.add(
      patternDrawable
    )

    layerList.add(
      GradientDrawable().also {
        it.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        it.cornerRadius =
          resources.getDimensionPixelOffset(R.dimen.acp_seek_progress_corner_radius)
            .toFloat()
        it.shape = GradientDrawable.RECTANGLE
        // TODO: Make stroke configurable
        //it.setStroke(
        //  4,
        //  Color.rgb(
        //    192,
        //    192,
        //    192
        //  )
        //)
      }
    )

    return layerList.toTypedArray()
  }

  override fun refreshProperties() {
    super.refreshProperties()
    // TODO: Pull to constants
    max = 100
  }
}
