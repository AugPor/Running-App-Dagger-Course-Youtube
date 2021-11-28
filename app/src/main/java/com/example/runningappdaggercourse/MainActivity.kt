package com.example.runningappdaggercourse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.runningappdaggercourse.db.RunDAO
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**Whenever we want to inject a field using Dagger-Hilt in an Android component (in this case, an Activity), we need to annotate it with
 * @AndroidEntryPoint. Then, we just need to declare the properties to be injected as lateinit var and use the @Inject annotation to make
 * Dagger look in its modules for a function to instantiate that property (in this case, it will find the functions in the AppModule).
  */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /**The @Inject annotation will make Dagger look for a function that creates an instance of the desired type in its modules (in our case, only
     * AppModule is installed in the Application scope).
     */
    @Inject
    lateinit var runDao: RunDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("runDAO","RunDAO: ${runDao.hashCode()}")
    }
}