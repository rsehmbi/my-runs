package com.example.ramanpreet_sehmbi

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult


class CustomDialog : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        var TITLE_KEY: String = "TITLE"
    }

    var DIALOG_TYPE = ""
    lateinit var editText: EditText;

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_custom_dialog, null)
        val bundle = arguments
        // Reference: Class Example
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        val title = bundle?.get(TITLE_KEY).toString()
        DIALOG_TYPE = title
        setEditTextProperties(view, title)
        builder.setTitle(title)
        editText = view.findViewById<EditText>(R.id.dialog_edit_text_id)
        builder.setPositiveButton("OK", this)
        builder.setNegativeButton("CANCEL", this)

        dialog = builder.create()
        return dialog
    }

    private fun setEditTextProperties(fragementView: View, titleText: String) {
        val editText = fragementView.findViewById<EditText>(R.id.dialog_edit_text_id)
        editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        if (titleText.contains("Comment")) {
            handleComment(editText)
        }
    }

    private fun handleComment(editText: EditText) {
        editText.setHint("How did it go? Notes here.")
        editText.inputType = InputType.TYPE_CLASS_TEXT
    }

    override fun onClick(dialog: DialogInterface?, positive_or_negative: Int) {
        val bundle = Bundle()
        if (positive_or_negative == DialogInterface.BUTTON_POSITIVE) {
            if (DIALOG_TYPE.contains("Duration")) {
                bundle.putString("DURATION_ENTERED", editText.text.toString())
            } else if (DIALOG_TYPE.contains("Distance")) {
                bundle.putString("DISTANCE_ENTERED", editText.text.toString())
            } else if (DIALOG_TYPE.contains("Calories")) {
                bundle.putString("CALORIES_ENTERED", editText.text.toString())
            } else if (DIALOG_TYPE.contains("Heart")) {
                bundle.putString("HEARTRATE_ENTERED", editText.text.toString())
            } else if (DIALOG_TYPE.contains("Comment")) {
                bundle.putString("COMMENT_ENTERED", editText.text.toString())
            }
            setFragmentResult("CUSTOM_DIALOG_REQUEST_KEY", bundle)
        }
    }

}