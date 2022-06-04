package com.example.ramanpreet_sehmbi

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class CameraGalleryDialog: DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        var CAMERA:String = "camera"
        var GALLERY:String = "gallery"
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog
        val options = arrayOf<CharSequence>("Open Camera", "Select from Gallery")
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Pick Profile Picture")
        builder.setItems(options,this)
        dialog = builder.create()
        return dialog
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        val selectedDateBundle = Bundle()
        if(p1 == 0){
            selectedDateBundle.putString("OPTION_SELECTED", CAMERA)
        }
        else if (p1 == 1){
            selectedDateBundle.putString("OPTION_SELECTED", GALLERY)
        }
        setFragmentResult("OPTION_REQUEST_KEY",selectedDateBundle)
    }
}