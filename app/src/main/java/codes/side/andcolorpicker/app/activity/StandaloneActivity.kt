package codes.side.andcolorpicker.app.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import codes.side.andcolorpicker.app.R
import kotlinx.android.synthetic.main.activity_standalone.*

class StandaloneActivity : AppCompatActivity(R.layout.activity_standalone) {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    defaultSeekBar.progress = 50
  }
}
