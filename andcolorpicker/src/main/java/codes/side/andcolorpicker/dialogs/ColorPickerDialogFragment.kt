package codes.side.andcolorpicker.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import codes.side.andcolorpicker.R

// TODO: Accept initial color value
// TODO: Accept resources
// TODO: Propagate picked value
// TODO: Support out-of-box color model selection
class ColorPickerDialogFragment : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return AlertDialog.Builder(requireActivity())
      .setTitle(R.string.title_dialog_pick)
      .setMessage(R.string.title_dialog_pick_message)
      .setView(
        R.layout.layout_dialog_hsla
      )
      .setPositiveButton(
        R.string.action_dialog_pick_positive
      ) { _, _ ->

      }
      .setNegativeButton(
        R.string.action_dialog_pick_negative
      ) { _, _ ->

      }
      .create()
  }
}
