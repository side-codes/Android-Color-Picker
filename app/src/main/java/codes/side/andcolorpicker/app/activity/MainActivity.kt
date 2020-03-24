package codes.side.andcolorpicker.app.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import codes.side.andcolorpicker.app.ColorizationConsumer
import codes.side.andcolorpicker.app.R
import codes.side.andcolorpicker.app.fragment.*
import codes.side.andcolorpicker.converter.toContrastColor
import codes.side.andcolorpicker.converter.toOpaqueColorInt
import codes.side.andcolorpicker.model.IntegerHSLColor
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.materialdesigndx.MaterialDesignDx
import com.mikepenz.iconics.utils.color
import com.mikepenz.iconics.utils.icon
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
  ColorizationConsumer {

  companion object {
    private const val TAG = "MainActivity"
  }

  private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
  private val colorizeHSLColorCache = IntegerHSLColor()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)

    actionBarDrawerToggle = ActionBarDrawerToggle(
      this,
      root,
      toolbar,
      com.mikepenz.materialdrawer.R.string.material_drawer_open,
      com.mikepenz.materialdrawer.R.string.material_drawer_close
    )

    slider.apply {
      itemAdapter.add(
        Page
          .values().map { page ->
            PrimaryDrawerItem().also {
              it.tag = page
              it.identifier = page.hashCode().toLong()
              it.name = StringHolder(page.title)
              it.icon = ImageHolder(
                IconicsDrawable(this@MainActivity)
                  .icon(page.icon)
                  .color { IconicsColor.colorRes(R.color.colorPrimary) }
              )
              it.isSelectable = page.fragmentProducer != null
            }
          }
      )

      onDrawerItemClickListener = { _, drawerItem, _ ->
        navigateTo(drawerItem.tag as Page)
        false
      }

      setSavedInstance(savedInstanceState)
    }

    if (savedInstanceState == null) {
      // TODO: Get rid of hashCode-based identifiers
      slider.setSelection(
        Page.HLS_SEEK_BAR.hashCode().toLong()
      )
    }
  }

  private fun navigateTo(page: Page) {
    when (page) {
      Page.GITHUB -> {
        startActivity(
          Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://github.com/side-codes/andColorPicker")
          )
        )
      }
      Page.OSS -> {
        startActivity(
          Intent(
            this,
            OssLicensesMenuActivity::class.java
          )
        )
      }
      else -> {
        if (supportFragmentManager
            .findFragmentByTag(page.toString()) != null
        ) {
          return
        }
        page.fragmentProducer?.let {
          supportFragmentManager
            .beginTransaction()
            .replace(
              R.id.fragmentContainer,
              it.invoke(),
              page.toString()
            )
            .commit()
        }
      }
    }
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    actionBarDrawerToggle.onConfigurationChanged(newConfig)
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    actionBarDrawerToggle.syncState()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      else -> {
        return actionBarDrawerToggle.onOptionsItemSelected(item)
      }
    }
  }

  // TODO: Propose that for
  //  https://github.com/mikepenz/MaterialDrawer/blob/develop/app/src/main/java/com/mikepenz/materialdrawer/app/AdvancedActivity.kt#L192
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(slider.saveInstanceState(outState))
  }

  override fun onBackPressed() {
    if (root.isDrawerOpen(slider)) {
      root.closeDrawer(slider)
    } else {
      super.onBackPressed()
    }
  }

  override fun colorize(color: IntegerHSLColor) {
    val contrastColor = color.toContrastColor()

    // Overwrite cache for AppBar
    colorizeHSLColorCache.setFrom(color)
    colorizeHSLColorCache.floatL = colorizeHSLColorCache.floatL.coerceAtMost(0.8f)

    appBarLayout.backgroundTintList =
      ColorStateList.valueOf(colorizeHSLColorCache.toOpaqueColorInt())

    // Overwrite cache for StatusBar
    colorizeHSLColorCache.floatL -= 0.1f

    window.statusBarColor = colorizeHSLColorCache.toOpaqueColorInt()

    toolbar.setTitleTextColor(contrastColor)
    toolbar.setSubtitleTextColor(contrastColor)

    actionBarDrawerToggle.drawerArrowDrawable.color = contrastColor
  }

  enum class Page(
    val title: String,
    val icon: IIcon,
    val fragmentProducer: (() -> Fragment)? = null
  ) {
    HLS_SEEK_BAR(
      "HSL SeekBar",
      MaterialDesignDx.Icon.gmf_space_bar,
      { HSLSeekBarFragment() }
    ),
    HLS_SEEK_BAR_RV(
      "HSL SeekBar RecyclerView",
      MaterialDesignDx.Icon.gmf_list,
      { HSLSeekBarRecyclerViewFragment() }
    ),
    HLS_SEEK_BAR_GITHUB(
      "HSL SeekBar GitHub",
      FontAwesome.Icon.faw_github,
      { HSLSeekBarGithubSampleFragment() }
    ),
    HLS_PLANE(
      "HSL Plane",
      MaterialDesignDx.Icon.gmf_fullscreen,
      { WipFragment() }
    ),
    CMYK_SEEK_BAR(
      "CMYK SeekBar",
      MaterialDesignDx.Icon.gmf_print,
      { CMYKSeekBarFragment() }
    ),
    RGB_SEEK_BAR(
      "RGB SeekBar",
      MaterialDesignDx.Icon.gmf_space_bar,
      { WipFragment() }
    ),
    RGB_PLANE(
      "RGB Plane",
      MaterialDesignDx.Icon.gmf_fullscreen,
      { WipFragment() }
    ),
    RGB_CIRCLE(
      "RGB Circle",
      MaterialDesignDx.Icon.gmf_lens,
      { WipFragment() }
    ),
    SWATCHES(
      "Swatches",
      MaterialDesignDx.Icon.gmf_view_comfy,
      { SwatchFragment() }
    ),
    GITHUB(
      "GitHub",
      FontAwesome.Icon.faw_github
    ),
    OSS(
      "Open Source Notices",
      MaterialDesignDx.Icon.gmf_emoji_people
    )
  }
}
