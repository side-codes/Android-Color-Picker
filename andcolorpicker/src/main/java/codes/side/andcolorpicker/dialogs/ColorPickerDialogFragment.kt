package codes.side.andcolorpicker.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import codes.side.andcolorpicker.R

class ColorPickerDialogFragment : DialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(requireActivity())
      .setMessage("Pick a color")
      .setView(
        R.layout.layout_hsla_dialog
      )
      .setPositiveButton(
        "Pick"
      ) { dialog, id ->

      }
      .setNegativeButton(
        "Cancel"
      ) { dialog, id ->

      }
      .create()
  }
}
