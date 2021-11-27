package com.example.runningappdaggercourse.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDAO {

    //Note: Room's DAO documentation explains that one-shot read and write queries can be done asynchronously using suspend functions, while observable reads can use the Jetpack Lifecycle's LiveData type.

    @Insert(onConflict = OnConflictStrategy.REPLACE)            //The OnConflictStrategy.REPLACE configured makes this function replace an existing Run in the database if we try to insert a Run with the same primary key.
    suspend fun insertRun(run: Run)                     //We use a suspend function to use it in an coroutine (freeing resources while we handle the database).

    @Delete
    suspend fun deleteRun(run: Run)                     //We use a suspend function to use it in an coroutine (freeing resources while we handle the database).


    /**Functions for sorting all runs according to parameter.*/
    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>         //We can't use a suspend function for this case, as it needs to return a LiveData for us to observe and we can't do that in a coroutine.

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>


    /**Functions that return the total statistics from all the recorded runs.*/
    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeed(): LiveData<Float>
}