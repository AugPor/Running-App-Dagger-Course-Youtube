package com.example.runningappdaggercourse.other

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

/**Object for utility functions, such as checking if the location permissions are enabled.
 */
object TrackingUtility {

    /**Checks if the location permissions were provided.*/
    fun hasLocationPermissions(context: Context): Boolean =

        /**As explained in the manifest, the background location permission is only requested from Android Q onwards, while previous versions
         * enable location in the background by default if the fine and coarse location permissions are provided. So, we check the version and
         * use the EasyPermissions library to confirm if the desired permissions were provided.
         */
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(                     //EasyPermissions' hasPermissions function receives the context and the permissions to check.
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
}