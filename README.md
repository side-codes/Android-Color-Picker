# andColorPicker

*:avocado: Handy, :snake: flexible, and :zap: lightning-fast material Android color picker view components*

![andColorPicker logo](https://github.com/side-codes/andColorPicker/raw/master/github/logo.png)

:speech_balloon: Work-In-Progress

## :pill: Features

- Clean, easy-to-use components and API
- High performance
- Material styling in mind
- Standard Android SDK view family
- Wide color models support
- Tooling and utilities
- Alpha channel support
- Cutting edge tech stack
- Active development and support

## :hammer: Setup

Gradle dependency:

```gradle
implementation "codes.side:andcolorpicker:0.4.0"
```

## :art: Picker types

### HSL (hue, saturation, lightness)

- *Add color model description*

![](https://github.com/side-codes/andColorPicker/raw/master/github/seek_bar_hsl_pure.png)

#### Layout XML Snippet

Basic HSL components:
```xml
<codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
  android:id="@+id/hueSeekBar"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  app:hslColoringMode="pure"
  app:hslMode="hue" />
```

Supported `hslMode` values:
- `hue` (default)
- `saturation`
- `lightness`

Supported `hslColoringMode` values:
- `pure` (default)
- `output`

Alpha component:
```xml
<codes.side.andcolorpicker.alpha.HSLAlphaColorPickerSeekBar
  android:id="@+id/alphaSeekBar"
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```

#### Kotlin Snippet
```kotlin
// Configure color model programmatically
hueSeekBar.mode = Mode.MODE_HUE // Mode.MODE_SATURATION, Mode.MODE_LIGHTNESS

// Configure coloring mode programmatically
hueSeekBar.coloringMode = ColoringMode.PURE_COLOR // ColoringMode.OUTPUT_COLOR

// Group pickers with PickerGroup to automatically synchronize color across them
val group = PickerGroup<IntegerHSLColor>().also {
  it.registerPickers(
    hueSeekBar,
    saturationSeekBar,
    lightnessSeekBar,
    alphaSeekBar
  )
}

// Get current color immediately
Log.d(
  TAG,
  "Current color is ${hueSeekBar.pickedColor}"
)

// Listen individual pickers or groups for changes
group.addListener(
  object : HSLColorPickerSeekBar.DefaultOnColorPickListener() {
    override fun onColorChanged(
      picker: ColorSeekBar<IntegerHSLColor>,
      color: IntegerHSLColor,
      value: Int
    ) {
      Log.d(
        TAG,
        "$color picked"
      )
      swatchView.setSwatchColor(
        color
      )
    }
  }
)

// Set desired color programmatically
group.setColor(
  IntegerHSLColor().also {
    it.setFromColorInt(
      Color.rgb(
        28,
        84,
        187
      )
    )
  }
)

// Set color components programmatically
hueSeekBar.progress = 50
```

### RGB (red, green, blue)

![](https://github.com/side-codes/andColorPicker/raw/master/github/seek_bar_rgb_pure.png)

#### Properties

- View name: ```RGBColorPickerSeekBar```
- ```app:rgbMode``` for RGB component selection

### LAB

![](https://github.com/side-codes/andColorPicker/raw/master/github/seek_bar_lab_output.png)

#### Properties

- View name: ```LABColorPickerSeekBar```
- ```app:labMode``` for LAB component selection

### CMYK (cyan, magenta, yellow, key)

![](https://github.com/side-codes/andColorPicker/raw/master/github/seek_bar_cmyk_pure.png)

#### Properties

- View name: ```CMYKColorPickerSeekBar```
- ```app:cmykMode``` for CMYK component selection
- ```app:cmykColoringMode``` for coloring mode selection

Supported `cmykMode` values:
- `cyan` (default)
- `magenta`
- `yellow`
- `black`

Supported `cmykColoringMode` values:
- `pure` (default)
- `output`

### Swatches

SwatchView component:
```xml
<codes.side.andcolorpicker.view.swatch.SwatchView
  android:id="@+id/swatchView"
  android:layout_width="match_parent"
  android:layout_height="wrap_content" />
```

#### Kotlin Snippet:
```kotlin
swatchView.setSwatchPatternTint(
  Color.LTGRAY
)

swatchView.setSwatchColor(
  IntegerHSLColor().also {
    it.setFromColorInt(
      ColorUtils.setAlphaComponent(
        Color.MAGENTA,
        128
      )
    )
  }
)
```

## :memo: License

```
Copyright 2020 Illia Achour

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
