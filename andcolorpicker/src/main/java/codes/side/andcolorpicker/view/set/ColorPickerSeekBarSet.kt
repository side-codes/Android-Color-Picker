package codes.side.andcolorpicker.view.set

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.TextViewCompat
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
        TextViewCompat.setTextAppearance(
          it,
          defaultTextAppearance
        )
        it.setText(titleResId)
      }
    )
  }

  companion object {
    private const val TAG = "ColorPickerSeekBarSet"
    private const val defaultTextAppearance = android.R.style.TextAppearance_Material_Caption
  }
}
