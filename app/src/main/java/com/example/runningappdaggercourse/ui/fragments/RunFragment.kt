package com.example.runningappdaggercourse.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningappdaggercourse.R
import com.example.runningappdaggercourse.databinding.FragmentRunBinding
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

    lateinit var binding: FragmentRunBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_run,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }
}