package com.example.ramanpreet_sehmbi

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment


class CustomDialog: DialogFragment(), DialogInterface.OnClickListener {
    companion object{
        var TITLE_KEY:String = "TITLE"
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_custom_dialog, null)
        val bundle = arguments
        // Reference: Class Example
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        val title = bundle?.get(TITLE_KEY).toString()
        setEditTextProperties(view, title)
        builder.setTitle(title)

        builder.setPositiveButton("OK", this)
        builder.setNegativeButton("CANCEL", this)

        dialog = builder.create()
        return dialog
    }

    private  fun setEditTextProperties(fragementView: View, titleText:String){
        val editText = fragementView.findViewById<EditText>(R.id.dialog_edit_text_id)
        if (titleText.contains("Comment")){
            handleComment(editText)
        }
    }
    private fun handleComment(editText:EditText){
        editText.setHint("How did it go? Notes here.")
    }

    override fun onClick(dialog: DialogInterface?, positive_or_negative: Int) {
        if(positive_or_negative == DialogInterface.BUTTON_POSITIVE){
            // Requirements not specified yet
        }
        else if(positive_or_negative == DialogInterface.BUTTON_NEGATIVE)
        {
            // Requirements not specified yet
        }
    }

}