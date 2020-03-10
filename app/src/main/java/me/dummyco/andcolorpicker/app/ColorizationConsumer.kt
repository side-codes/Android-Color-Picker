package me.dummyco.andcolorpicker.app

import me.dummyco.andcolorpicker.model.IntegerHSLColor

interface ColorizationConsumer {
  fun colorize(color: IntegerHSLColor)
}
