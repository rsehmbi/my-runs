package com.example.ramanpreet_sehmbi.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.LatLng
import java.util.*


@Entity(tableName= "exercise_entry_table")
data class ExerciseEntry (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "input_type_column")
    var inputType: String = "",

    @ColumnInfo(name = "activity_type_column")
    var activityType: String = "",

    @ColumnInfo(name = "date_time_column")
    var dateTime: Calendar,

    @ColumnInfo(name = "duration_column")
    var duration: Int,

    // Will always be stored in kilometers and converted
    // as required on saving and retrieving
    @ColumnInfo(name = "distance_column")
    var distance: Float,

    @ColumnInfo(name = "avg_pace_column")
    var avgPace: Float,

    @ColumnInfo(name = "avg_speed_column")
    var avgSpeed: Float,

    @ColumnInfo(name = "calorie_column")
    var calorie: Float,

    @ColumnInfo(name = "climb_column")
    var climb: Float,

    @ColumnInfo(name = "heart_rate_column")
    var heartrate: Int,

    @ColumnInfo(name = "comment_column")
    var comment: String = "",

    @ColumnInfo(name = "location_list_column")
    var locationList: ArrayList<LatLng>,
)