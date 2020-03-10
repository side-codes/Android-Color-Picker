package me.dummyco.andcolorpicker.app.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class DelayFreeScrollView : ScrollView {
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
