package com.example.runningappdaggercourse.ui.fragments

import android.Manifest
import android.os.Build
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
import com.example.runningappdaggercourse.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runningappdaggercourse.other.TrackingUtility
import com.example.runningappdaggercourse.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**As this is a Android component (Fragment), we need to annotate it with @AndroidEntryPoint (only ViewModel and Application use different
 * annotations).
 */
@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {       //By implementing EasyPermissions' PermissionCallbacks interface, we can use implement callback functions for when the permission is granted or denied.

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

        requestPermissions()

        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    /**In order to use the app, we will need to request the permissions. For that, we can use the function below, which first checks whether
     * the permission were already accepted and requests them if not (following the logic that the request for background location is separate
     * for Android Q and later versions).
     */
    private fun requestPermissions() {
        if(TrackingUtility.hasLocationPermissions(requireContext())) return        //We need to get the context from the Activity. But, it's better to use requireContext() than using activity.context, as activity is nullable.

        /**As explained in the Manifest file, versions from Android Q onwards need to accept background location separately from fine and
         * coarse location permissions. So, we first check the SDK version and use EasyPermissions library's requestPermissions function to
         * request the permissions accordingly. As we will use it in a fragment, its parameters are: a fragment where the request is being
         * made, a rationale (the popup message requesting the user to accept the permission), a request code (an integer identifier of the
         * request) and the permissions.
         * One advantage of using the EasyPermissions library in this case is that, if the user denies access to a permission many times, it
         * will permanently deny the permission, but EasyPermissions can detect that and instruct the user to accept the permission in the
         * app settings.
         */
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use the app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use the app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    //This method is from the PermissionCallbacks interface and executes when permissions are granted.
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    //This method is from the PermissionCallbacks interface and executes when permissions are denied. We can use this to check if the permission was permanently denied and guide the user to the app settings if so.
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)) {
            //This will show the app settings dialog for the user to remove the permanent denial of permissions.
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    /*This method is from the Android framework and it's called when there is a result of the permission request. We can use this method to
    call the EasyPermissions method of same name, passing the same parameters, to let EasyPermissions know the request result. This is what
    will actually make the onPermissionsGranted and onPermissionsDenied methods get called.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        /*EasyPermissions' method takes in the same arguments as the Android framework's method, plus the receiver. The receiver is the class
        that implements the EasyPermissions.PermissionCallbacks interface, i.e. this method receives the result of the permission request and
        calls the receiver's onPermissionsGranted and onPermissionsDenied methods accordingly.
         */
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}