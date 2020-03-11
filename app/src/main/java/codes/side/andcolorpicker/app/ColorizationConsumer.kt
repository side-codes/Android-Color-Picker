package codes.side.andcolorpicker.app

import codes.side.andcolorpicker.model.IntegerHSLColorModel

interface ColorizationConsumer {
  fun colorize(color: IntegerHSLColorModel)
}
