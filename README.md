![](github/logo.png)

*:avocado: Handy, :snake: flexible and :zap: lightning-fast material color picking UI component for Android*

:speech_balloon: Work-In-Progress
## Roadmap

- [ ] Add more picker types
    - [x] HLS seekbars
    - [ ] RGB seekbars
    - [ ] RGB circle
    - [ ] RGB plane
    - [ ] Alpha seekbar
    - [ ] HSL (S+L) plane
    - [ ] Swatches
- [ ] Sample buttons -> radios
- [ ] Enhance API
- [x] Add XML attributes
- [ ] Add thumb animation
- [x] Add *MaterialDrawer* & sample fragments
- [ ] Add more *HSLColorPickerSeekBar* checks and reduce calls count
- [ ] Add more encapsulation to limit picker modification capabilities
- [ ] Package repository publish
- [ ] Add Rx support
- [ ] Add/Revisit *RecyclerView* support
- [x] Add sample app icon
- [ ] Add logger solution
- [ ] Add sample app analytics
- [ ] Add call flow diagram
- [ ] Add tests
- [ ] Add docs
- [ ] Add contribution guidelines
- [x] Add OSS licenses
- [x] Add license

## Picker types

### HSL (hue, saturation, lightness)

- *Add color model description*
- *Add usage sample*

![](github/type_hsl.png)

#### Layout XML Snippet
```
<me.dummyco.andcolorpicker.HSLColorPickerSeekBar
  android:id="@+id/andColorPicker"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  app:coloring="pure"
  app:mode="hue" />
```

#### Kotlin Snippet
```
andColorPicker.addListener(
  object : HSLColorPickerSeekBar.DefaultOnColorPickListener() {
    override fun onColorChanged(
      picker: HSLColorPickerSeekBar,
      color: DiscreteHSLColor,
      mode: HSLColorPickerSeekBar.Mode,
      value: Int
    ) {
      // Client code
    }
  }
)
```

## License

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
