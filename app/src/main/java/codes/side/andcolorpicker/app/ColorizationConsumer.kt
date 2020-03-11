package codes.side.andcolorpicker.app

import codes.side.andcolorpicker.model.IntegerHSLColor

interface ColorizationConsumer {
  fun colorize(color: IntegerHSLColor)
}
