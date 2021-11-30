package com.example.runningappdaggercourse.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningappdaggercourse.R
import com.example.runningappdaggercourse.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**As this is a Android component (Fragment), we need to annotate it with @AndroidEntryPoint (only ViewModel and Application use different
 * annotations).
 */
@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run) {

    /**To inject ViewModels, we don't use the @Inject annotation. We can just use "by viewModels()" from the KTX extension (it's in the app's
     * build.gradle file). As Dagger manages all ViewModels behind the scenes, it will select the correct one and assign it here.
     */
    private val viewModel: MainViewModel by viewModels()
}