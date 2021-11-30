package com.example.runningappdaggercourse.repositories

import com.example.runningappdaggercourse.db.Run
import com.example.runningappdaggercourse.db.RunDAO
import javax.inject.Inject

/**The job of the Repository class is to just collect data from other data sources (Room databases, APIs etc.).
 */
class MainRepository @Inject constructor(
    val runDao: RunDAO
) {

    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()
    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()
    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()
    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
}