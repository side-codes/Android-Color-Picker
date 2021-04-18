package codes.side.andcolorpicker.view.picker

import codes.side.andcolorpicker.model.IntegerCMYKColor
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.model.IntegerLABColor
import codes.side.andcolorpicker.model.IntegerRGBColor

typealias OnIntegerRGBColorPickListener = ColorSeekBar.DefaultOnColorPickListener<ColorSeekBar<IntegerRGBColor>, IntegerRGBColor>

typealias OnIntegerCMYKColorPickListener = ColorSeekBar.DefaultOnColorPickListener<ColorSeekBar<IntegerCMYKColor>, IntegerCMYKColor>

typealias OnIntegerHSLColorPickListener = ColorSeekBar.DefaultOnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor>

typealias OnIntegerLABColorPickListener = ColorSeekBar.DefaultOnColorPickListener<ColorSeekBar<IntegerLABColor>, IntegerLABColor>
