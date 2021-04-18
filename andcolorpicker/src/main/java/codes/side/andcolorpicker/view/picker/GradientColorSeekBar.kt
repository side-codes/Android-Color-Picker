package codes.side.andcolorpicker.view.picker

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import codes.side.andcolorpicker.R
import codes.side.andcolorpicker.model.Color
import codes.side.andcolorpicker.model.factory.ColorFactory

abstract class GradientColorSeekBar<C : Color> @JvmOverloads constructor(
  colorFactory: ColorFactory<C>,
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = R.attr.seekBarStyle
) : ColorSeekBar<C>(
  colorFactory,
  context,
  attrs,
  defStyle
) {

  override fun onSetupProgressDrawableLayers(layers: Array<Drawable>): Array<Drawable> {
    return layers.toMutableList().also { list ->
      list.add(
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
    }.toTypedArray()
  }
}
