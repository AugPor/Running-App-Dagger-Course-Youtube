package com.example.runningappdaggercourse.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runningappdaggercourse.R
import com.example.runningappdaggercourse.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningappdaggercourse.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runningappdaggercourse.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningappdaggercourse.other.Constants.ACTION_STOP_SERVICE
import com.example.runningappdaggercourse.other.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.example.runningappdaggercourse.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningappdaggercourse.other.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningappdaggercourse.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningappdaggercourse.other.Constants.NOTIFICATION_ID
import com.example.runningappdaggercourse.other.TrackingUtility
import com.example.runningappdaggercourse.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

//We can create custom types using typealias, which substitute complex types.
typealias Poliline = MutableList<LatLng>        //A Poliline can be seen as a continuous path in our app, composed of many individual LatLng points interconnected with lines.
typealias Polilines = MutableList<Poliline>     //Polilines would be the group of paths where the app was tracking the run.

/**Services are a component that can perform long-running operation (such as handle network transactions, play music, edit files, interact with
 * content providers etc.), even when the user is not interacting with the app, or that can supply functionality for other apps to use.
 * Different from Services, Threads (or Kotlin's coroutines) are used to do tasks in the background only while the user interacts with the app.
 * By default, Services still run in the main thread, so it needs to create a new thread if it's going to do intensive/blocking operations.
 * Commonly, service classes inherit from Service or from IntentService. In this case, as we want to observe LiveData in the service (and we
 * have to provide a LifecycleOwner to the observe method), we need to use LifecycleService.
 *
 * Service types are foreground (the operation that the service runs is perceptible to the user, needing to appear as a notification as well,
 * having the benefit of not being able to be killed by the OS), background (the service performs an action in the background, which can be
 * killed by the OS if it needs memory) and bound (a service that is bound to the app component(s) that called bindService, establishing a
 * client-server relation for requesting and returning data, and also is a service that is destroyed when no component is bound to it anymore).
 *
 * For this service, we will need to have the communication from activities/fragments to the service (for them to send commands to it) and also
 * communication from the service back to them (to return the user coordinates to TrackingFragment, for example). For sending commands to the
 * service, we can use Intents that are sent from the activity/fragment (in TrackingFragment's sendCommandToService method) and received in
 * the service's onStartCommand method. For retuning information from the service to the fragment, we can either use the singleton approach,
 * by creating a companion object in the service and accessing it in the activity/fragment, or make the service a bound service, in which the
 * activity/fragment binds to the service and basically creates a client-server interface between fragment and service, being able to send
 * requests and receive results. In our case, a bound service would be harder to implement and not really necessary, the singleton approach
 * was implemented.
 */
class TrackingService: LifecycleService() {

    var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()

//        val pathPoints = MutableLiveData<MutableList<MutableList<LatLng>>>()        //LatLng is an object that indicates the coordinates of a point in the map. To indicate a continuous path, we could use a list of LatLng objects, but as the user might want to pause the tracking, walk a little and later start tracking the run again, we need to consider these paths separately, needing a list of the paths (i.e. a list of lists of LatLng objects).
        val pathPoints = MutableLiveData<Polilines>()           //This line is the same as the above, but using a typealias created by us at the start of the file.
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()

        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this) {
            updateLocationTracking(it)
        }
    }

    /**The onStartCommand is called everytime the service receives a command (an Intent with an action attached to it, that was sent through
     * the startService method). Here, we check if the received intent is not null and then check the action to be executed.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resumed service.")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service.")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service.")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**This function adds a new Poliline, i.e. starts a new route that is being tracked. If the list of routes being tracked is not null
     * (there is already a route that was tracked), we add a new empty list that will represent the new route to track. If the list of
     * routes is null, we also create the list that will contain lists that represent the routes.
     */
    private fun addEmptyPoliline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    /**This function takes in a Location and adds it as a LatLng to the last Poliline.*/
    private fun addPathPoint(location: Location?) {
        location?.let {
            val position = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }

    /**We can use a Fused Location Provider to get the location at a consistent basis (periodically after a set interval or after
     * certain distance). We just need to provide a LocationRequest and a LocationCallback to the requestLocationUpdates method.
     * The LocationRequest contains the interval, which is the time in milliseconds around which the locations will be provided (can
     * be less or more), the fastestInterval, which limits how frequent the locations will be received (to avoid it receiving too many
     * locations and wasting power), and the priority, which determines if the priority is accuracy or power saving. The callback needs
     * to be an object of a class that implements LocationCallback, which will receive the resulting location and process it in its
     * onLocationResult method. We also need to provide a Looper, which is message loop in a thread.
     */
    @SuppressLint("MissingPermission")      //The method fusedLocationProviderClient.requestLocationUpdates requires a checking the location permission. As we are checking the permissions with EasyPermissions, in the hasLocationPermissions method, it doesn't recognize the permission check and it would indicate an error, which is why we need to suppress it.
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking) {
            if(TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    /**We can create a callback object to receive the location, which can be done with an anonymous class of LocationCallback, through
     * the onLocationResult method. The result contains a list of Locations, which can be added to the Poliline.
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!) {
                result?.locations?.let {locations ->
                    for(location in locations) {
                        addPathPoint(location)
                        Timber.d("Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                    }
                }
            }
        }
    }

    /**To make a foreground service, we need to configure it as so and also display the notification. For Android Oreo and later, we also
     * need to create a notification channel to display a notification (notification channels are the categories of notifications, which
     * allow the user to choose to display or to hide). To create a Notification object, we need to use the Builder pattern, with
     * NotificationCompat.Builder. Then, we pass the notification id and the Notification object to the startForeground function.
     */
    private fun startForegroundService() {

        addEmptyPoliline()      //When we start the service, we are also starting to track the location, so we have to add a Poliline to contain the route that will be tracked.
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager     //NotificationManager is a Android framework service that allows us to show our notifications.

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        //We use NotificationCompat.Builder to configure the Notification object to be created.
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)     //To create the Builder, we specify the context and the ID of the notification channel it's going to belong to.
            .setAutoCancel(false)               //Auto-cancel as false: the notification doesn't disappear if the user clicks on it.
            .setOngoing(true)                   //Ongoing as true: the notification can't be swiped away.
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())       //Configures the PendingIntent to be sent when the notification is clicked (check the explanation on the getMainActivityPendingIntent method).

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    /**PendingIntent is a description of an Intent that can be provided to other applications. When we give a PendingIntent to other
     * applications, they can execute that Intent as if they were the application itself. In this case, we are creating a PendingActivity
     * so that the notification can send an Intent in the app itself. We are using the getActivity method and providing an Intent that
     * indicates the activity we want. We also set the action of the intent to indicate that we want to load the TrackingFragment as soon
     * as the activity opens, which needs to be dealt in the MainActivity.
     */
    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT                 //Setting this flag means that when we launch a PendingIntent and it already exists, it will only update it, instead of restarting it.
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW                  //We need to choose IMPORTANCE_LOW here, because we need to show a notification every second to update the time and any importance level above low would trigger a sound notification.
        )
        notificationManager.createNotificationChannel(channel)
    }

}