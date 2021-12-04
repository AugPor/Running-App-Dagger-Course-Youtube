package com.example.runningappdaggercourse.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningappdaggercourse.R
import com.example.runningappdaggercourse.databinding.FragmentTrackingBinding
import com.example.runningappdaggercourse.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningappdaggercourse.services.TrackingService
import com.example.runningappdaggercourse.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {

    /**To inject ViewModels, we don't use the @Inject annotation. We can just use "by viewModels()" from the KTX extension (it's in the app's
     * build.gradle file). As Dagger manages all ViewModels behind the scenes, it will select the correct one and assign it here.
     */
    private val viewModel: MainViewModel by viewModels()

    lateinit var binding: FragmentTrackingBinding

    /**To use Google Maps in an app, in the XML file we can either use a MapView or a MapFragment, which is just a fragment that contains
     * a MapView. The MapFragment component is used to take advantage of the fragment's lifecycle functions to handle the map's lifecycle.
     * However, when we already have a fragment, it's best to use MapView, as using fragments inside fragments is not good for the
     * performance. Then, when using MapView, we need to handle the MapView's lifecycle by ourselves, by calling its lifecycle functions
     * inside the corresponding fragment's lifecycle functions (e.g. onStart, onResume, onPause etc.).
     * To get a map, we can use the MapView's getMapAsync method and we can set the callback function to be executed when it retrieves
     * the map, which can be saving it in a variable of type GoogleMap.
     */
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_tracking,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)

        binding.btnToggleRun.setOnClickListener {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }

        binding.mapView.getMapAsync{
            map = it
        }
    }

    /**The sendCommandToService function will send the command to start, pause or stop the service by using an intent with an action.*/
    private fun sendCommandToService(action: String) =
        Intent(requireContext(),TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)               //Although it's called startService, this method will actually send the intent to the service, where we will judge if we have to start, pause or stop the service based on the intent's action.
        }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    //onLowMemory is called when the device is running low on memory, in order to save some resources.
    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    //Helps caching the map that was loaded, so we don't need to load it again when we come back to the app.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}