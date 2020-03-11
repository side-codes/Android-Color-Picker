package codes.side.andcolorpicker.app.view

import android.content.Context
import android.util.AttributeSet
import androidx.drawerlayout.widget.DrawerLayout

class DelayFreeDelayDrawerLayout : DrawerLayout {
  constructor(context: Context) : super(context)

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : super(
    context,
    attrs
  )

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(
    context,
    attrs,
    defStyleAttr
  )

  override fun shouldDelayChildPressedState(): Boolean {
    return false
  }
}
