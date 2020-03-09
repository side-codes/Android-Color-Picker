package me.dummyco.andcolorpicker.app

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.materialdesigndx.MaterialDesignDx
import com.mikepenz.iconics.utils.icon
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.activity_main.*
import me.dummyco.andcolorpicker.model.DiscreteHSLColor

class MainActivity : AppCompatActivity(), ColorizationConsumer {

  companion object {
    private const val TAG = "MainActivity"
    private const val PRIMARY_DARK_LIGHTNESS_SHIFT = -10
  }

  private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

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
        Page.values().map { page ->
          PrimaryDrawerItem().also {
            it.tag = page
            it.identifier = page.hashCode().toLong()
            it.name = StringHolder(page.title)
            it.icon = ImageHolder(
              IconicsDrawable(this@MainActivity).icon(page.icon)
            )
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
      slider.setSelection(Page.HLS_SEEK_BAR.hashCode().toLong())
    }
  }

  private fun navigateTo(page: Page) {
    supportFragmentManager
      .beginTransaction()
      .replace(
        R.id.fragmentContainer,
        page.fragment.invoke()
      )
      .commit()
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

  override fun colorize(color: DiscreteHSLColor) {
    val contrastColor = color.createContrastColor()

    appBarLayout.backgroundTintList = ColorStateList.valueOf(color.colorInt)

    val statusBarColor = color.copy().also {
      it.intL += PRIMARY_DARK_LIGHTNESS_SHIFT
    }
    window.statusBarColor = statusBarColor.colorInt

    toolbar.setTitleTextColor(contrastColor)
    toolbar.setSubtitleTextColor(contrastColor)

    actionBarDrawerToggle.drawerArrowDrawable.color = contrastColor
  }

  enum class Page(
    val title: String,
    val icon: MaterialDesignDx.Icon,
    val fragment: () -> Fragment
  ) {
    HLS_SEEK_BAR(
      "HSL SeekBar",
      MaterialDesignDx.Icon.gmf_color_lens,
      { HslSeekBarFragment() }
    ),
    HLS_PLANE(
      "HSL Plane",
      MaterialDesignDx.Icon.gmf_color_lens,
      { WipFragment() }
    ),
    RGB_SEEK_BAR(
      "RGB SeekBar",
      MaterialDesignDx.Icon.gmf_color_lens,
      { WipFragment() }
    ),
    RGB_PLANE(
      "RGB Plane",
      MaterialDesignDx.Icon.gmf_color_lens,
      { WipFragment() }
    ),
    RGB_CIRCLE(
      "RGB Circle",
      MaterialDesignDx.Icon.gmf_color_lens,
      { WipFragment() }
    ),
    SWATCHES(
      "Swatches",
      MaterialDesignDx.Icon.gmf_color_lens,
      { WipFragment() }
    ),
  }
}
