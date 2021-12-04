package com.example.runningappdaggercourse.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.runningappdaggercourse.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningappdaggercourse.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningappdaggercourse.other.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

/**Services are a component that can perform long-running operation (such as handle network transactions, play music, edit files, interact with
 * content providers etc.), even when the user is not interacting with the app, or that can supply functionality for other apps to use.
 * Different from Services, Threads (or Kotlin's coroutines) are used to do tasks in the background only while the user interacts with the app.
 * By default, Services still run in the main thread, so it needs to create a new thread if it's going to do intensive/blocking operations.
 * Commonly, service classes inherit from Service or from IntentService. In this case, as we want to observe LiveData in the service (and we
 * have to provide a LifecycleOwner to the observe method), we need to use LifecycleService.
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

    /**The onStartCommand is called everytime the service receives a command (an Intent with an action attached to it, that was sent through
     * the startService method). Here, we check if the received intent is not null and then check the action to be executed.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("Started or resumed service.")
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
}