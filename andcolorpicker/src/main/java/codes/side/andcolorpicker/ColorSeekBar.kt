package codes.side.andcolorpicker

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import codes.side.andcolorpicker.model.ColorModel

abstract class ColorSeekBar<C : ColorModel> : AppCompatSeekBar,
  SeekBar.OnSeekBarChangeListener {

  private val colorPickListeners = hashSetOf<OnColorPickListener<C>>()
  abstract var currentColor: C

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

  open fun addListener(listener: OnColorPickListener<C>) {
    colorPickListeners.add(listener)
  }

  open fun removeListener(listener: OnColorPickListener<C>) {
    colorPickListeners.remove(listener)
  }

  fun clearListeners() {
    colorPickListeners.clear()
  }

  override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
    if (l != this) {
      throw IllegalStateException("Custom OnSeekBarChangeListener not supported yet")
    }
    super.setOnSeekBarChangeListener(l)
  }

  protected fun notifyListenersOnColorChanged() {
    colorPickListeners.forEach {
      it.onColorChanged(
        this,
        currentColor,
        progress
      )
    }
  }

  protected fun notifyListenersOnColorPicking(fromUser: Boolean) {
    colorPickListeners.forEach {
      it.onColorPicking(
        this,
        currentColor,
        progress,
        fromUser
      )
    }
  }

  protected fun notifyListenersOnColorPicked(fromUser: Boolean) {
    colorPickListeners.forEach {
      it.onColorPicked(
        this,
        currentColor,
        progress,
        fromUser
      )
    }
  }

  override fun onStartTrackingTouch(seekBar: SeekBar) {
  }

  override fun onStopTrackingTouch(seekBar: SeekBar) {
    colorPickListeners.forEach {
      it.onColorPicked(
        this,
        currentColor,
        progress,
        true
      )
    }
  }

  // TODO: Rename
  interface OnColorPickListener<C : ColorModel> {
    fun onColorPicking(
      picker: ColorSeekBar<C>,
      color: C,
      value: Int,
      fromUser: Boolean
    )

    fun onColorPicked(
      picker: ColorSeekBar<C>,
      color: C,
      value: Int,
      fromUser: Boolean
    )

    fun onColorChanged(
      picker: ColorSeekBar<C>,
      color: C,
      value: Int
    )
  }
}
