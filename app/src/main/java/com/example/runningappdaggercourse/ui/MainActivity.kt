package com.example.runningappdaggercourse.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.runningappdaggercourse.R
import com.example.runningappdaggercourse.databinding.ActivityMainBinding
import com.example.runningappdaggercourse.db.RunDAO
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**Whenever we want to inject a field using Dagger-Hilt in an Android component (in this case, an Activity), we need to annotate it with
 * @AndroidEntryPoint. Then, we just need to declare the properties to be injected as lateinit var and use the @Inject annotation to make
 * Dagger look in its modules for a function to instantiate that property (in this case, it will find the functions in the AppModule).
 * Other possibility is to annotate the constructor of the class with @Inject, making Dagger look for how to create the dependencies used
 * in the constructor.
  */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /**The @Inject annotation will make Dagger look for a function that creates an instance of the desired type in its modules (in our case, only
     * AppModule is installed in the Application scope).
     */
//    @Inject                           //Done just for testing (we don't want to use our database DAO in an activity.
//    lateinit var runDao: RunDAO

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
//        setContentView(R.layout.activity_main)


        setSupportActionBar(findViewById(R.id.toolbar))

        //Android recommends us using a FragmentContainerView to house the fragments now (instead of a <fragment>) and the documentation recommends that we do the process described below to access the NavController and be able to associate it with other components, like the BottomNavigationView.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
//        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setupWithNavController(findViewById(R.id.navHostFragment).findNavController())

        //Hide the bottom navigation menu (which is part of the activity) when in the setup and settings fragment.
        binding.let {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                    when (destination.id) {
                        R.id.runFragment, R.id.settingsFragment, R.id.statisticsFragment ->
                            it.bottomNavigationView.visibility = View.VISIBLE
                        else -> it.bottomNavigationView.visibility = View.GONE
                    }
                }
        }

    }
}