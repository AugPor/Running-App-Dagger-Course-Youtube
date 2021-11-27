package com.example.runningappdaggercourse.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run (                        //We will record information of each run (image, date, distance, speed) in this data class.
    var img: Bitmap? = null,            //The img parameter will store the preview of the route (a screenshot of the map with the run route).
    var timestamp: Long = 0L,           //The time when the run was started, as a Long value that represents the time in milliseconds from a certain date (that can be converted into a date). Using milliseconds is recommended because it's accurate and can be easily converted into other time measurements.
    var timeInMillis: Long = 0L,        //The duration of the run in milliseconds (it's recommended that we always use the same time unit for different variables).
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var caloriesBurned: Int = 0
        ) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}
