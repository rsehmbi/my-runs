package com.example.ramanpreet_sehmbi.CustomAdapters


import android.app.Activity
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.ramanpreet_sehmbi.R
import com.example.ramanpreet_sehmbi.UIHelpers.convertMetrics
import com.example.ramanpreet_sehmbi.ViewModels.UnitViewModel

class HistoryListAdapter(
    private val context: Activity,
    private val id: List<String>,
    private val entryType: List<String>,
    private val activityType: List<String>,
    private val datetime: List<String>,
    private val distance: List<String>,
    private val duration: List<String>,
    private val metric_system: String,
) : ArrayAdapter<String>(context, R.layout.custom_list_item, id) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_item, null, true)

        val customeEntryType = rowView.findViewById<TextView>(R.id.entry_type_id)
        val customActivityType = rowView.findViewById<TextView>(R.id.activity_type_id)
        val customDateTime = rowView.findViewById<TextView>(R.id.date_time_id)
        val customDistance = rowView.findViewById<TextView>(R.id.distance_id)
        val customDuration = rowView.findViewById<TextView>(R.id.duration_id)

        customeEntryType.text = entryType[position]
        customActivityType.text = activityType[position]
        customDateTime.text = datetime[position]
        customDistance.text = convertMetrics(distance[position], metric_system)
        customDuration.text = duration[position]

        return rowView
    }
}