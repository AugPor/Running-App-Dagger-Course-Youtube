package com.example.runningappdaggercourse.ui.fragments

import androidx.fragment.app.Fragment
import com.example.runningappdaggercourse.R

class SettingsFragment: Fragment(R.layout.fragment_settings) {      //We won't need a ViewModel for this fragment, as it won't have any information to persist for configuration changes (the weight and name will be saved via SharedPreferences).
}