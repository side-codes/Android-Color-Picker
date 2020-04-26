package codes.side.andcolorpicker.view.set

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.model.Color

abstract class ColorPickerSeekBarSet<C : Color> @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : LinearLayout(
  context,
  attrs,
  defStyle
) {
  companion object {
    private const val TAG = "ColorPickerSeekBarSet"
    internal const val defaultTextAppearance = android.R.style.TextAppearance_Material_Caption
  }

  val pickerGroup = PickerGroup<C>()

  init {
    orientation = VERTICAL
    init(attrs)
  }

  private fun init(attrs: AttributeSet? = null) {
    //val typedArray = context.theme.obtainStyledAttributes(
    //  attrs,
    //  R.styleable.ColorPickerSeekBarSet,
    //  0,
    //  0
    //)

    //typedArray.recycle()
  }

  protected fun addLabel(@StringRes titleResId: Int) {
    addView(
      TextView(
        context
      ).also {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
          @Suppress("DEPRECATION")
          it.setTextAppearance(
            context,
            defaultTextAppearance
          )
        } else {
          it.setTextAppearance(defaultTextAppearance)
        }
        it.setText(titleResId)
      }
    )
  }
}
