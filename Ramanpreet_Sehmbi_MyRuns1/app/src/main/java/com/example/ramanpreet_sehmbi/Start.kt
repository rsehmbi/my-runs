package com.example.ramanpreet_sehmbi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment


class Start : Fragment(){

    lateinit var inputTypeSpinner: Spinner
    lateinit var activityTypeSpinner: Spinner
    lateinit var startButton: Button

    var INPUTTYPE = "Manual Entry"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentStartView = inflater.inflate(R.layout.fragment_start, container, false)

        inputTypeSpinner = fragmentStartView.findViewById(R.id.input_type_id)
        val inputTypeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.input_types, android.R.layout.simple_list_item_1);
        inputTypeSpinner.adapter = inputTypeAdapter
        inputTypeSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                INPUTTYPE = "Manual Entry"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                INPUTTYPE = type
            }

        }

        activityTypeSpinner = fragmentStartView.findViewById(R.id.activity_types_id)
        val activityTypeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.activity_types, android.R.layout.simple_list_item_1);
        activityTypeSpinner.adapter = activityTypeAdapter
        activityTypeSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO: Requirement not specified yet.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //TODO: Requirement not specified yet.
            }
        }

        startButton = fragmentStartView.findViewById(R.id.start_button_id)
        startButton.setOnClickListener(){
            if(INPUTTYPE == "Manual Entry"){
                startActivity(Intent(activity, ManualEntry::class.java))
            }
            else{
                startActivity(Intent(activity, GPS::class.java))
            }
        }


        return fragmentStartView
    }

}