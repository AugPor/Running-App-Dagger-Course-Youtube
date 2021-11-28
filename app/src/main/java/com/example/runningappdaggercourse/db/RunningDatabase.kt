package com.example.runningappdaggercourse.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Run::class], version = 1)         //To create the Room database, we use the @Database annotation, passing entities as a list of data classes annotated with @Entity and a version (a number that needs to change as the database schema changes).
@TypeConverters(Converters::class)                      //The @TypeConverters Room annotation indicates the class that contains the functions annotated with @TypeConverter, to convert complex data types into ones that can be saved in the database.

/*Usually, we need to implement a bigger logic in Room databases, in order to make them a singleton, creating them only once and only returning
that instance, besides having a way to access the DAO. However, when we use dependency injection tools, such as Dagger, we can leave the
singleton logic to it, and our implementation can be simplified.
 */
abstract class RunningDatabase: RoomDatabase() {
    abstract fun getRunDao(): RunDAO
}