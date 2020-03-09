package me.dummyco.andcolorpicker.app

import me.dummyco.andcolorpicker.model.DiscreteHSLColor

interface ColorizationConsumer {
  fun colorize(color: DiscreteHSLColor)
}
