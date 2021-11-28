package com.example.runningappdaggercourse

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**One class is said to have dependencies if it needs certain objects in order to work properly. This could be parameters for a constructor or
 * a database reference that needs to be passed into a ViewModel class, for example. Dependency injection is the act of providing the
 * dependencies to a class.
 * There are a few libraries for handling dependency injection, that enable us to configure the dependencies that exist between classes and
 * make the process of instantiating a class that has dependencies simpler. For example, if we needed to create a ViewModel class that has the
 * reference to the project's repository, which in turn have a reference to the database, we can just use a dependency injection library to
 * provide these dependencies to the ViewModelFactory, without needing to actually create references to the database and repository in the code
 * to create the ViewModel (if we were creating the ViewModel from an activity and were using MVVM, this would break the architecture, as we
 * would have instances of Model classes in a View class).
 * Another advantage from using dependency injection libraries is that we can create singletons (classes with only a single instance) and
 * manage their lifetime according to the scope (for example, if we need a singleton only for a login activity, but not for the other activities,
 * we can adjust the singleton's lifetime for just the scope of the login activity).
 * Some popular options available for dependency injection are Dagger (maintained by Google) and Koin. One difference between these two is that
 * Koin doesn't generate extra code beside the files used to set Koin up and define the dependencies. Another difference is that Koin is
 * actually a service locator (not real dependency injection) and injects them at runtime, while Dagger performs the dependency injection at
 * compile time, being more performant. To check a short example of the Koin library implementation, check the "Notes (MVVM course)" file.
 *
 * Hilt is a library built on Dagger and is the Jetpack's recommended one for dependency injection in Android. To use it, we need to create an
 * Application class, set it in the name property of the application tag in the manifest file, annotate it with Dagger-Hilt @HiltAndroidApp and
 * rebuild the project. After that, Dagger will generate Java files that handle dependency injection. This class becomes the application-level
 * dependency container. Besides the Application class, Hilt can provide dependencies for other Android classes, such as ViewModel (using the
 * @HiltViewModel annotation) and the following classes using the @AndroidEntryPoint annotation: Activity, Fragment, View, Service and
 * BroadcastReceiver.
 * To have a dependency be injected in a class, all we need to do is instantiate it as a lateinit in the class and annotate it with @Inject, or
 * simply annotate the class' constructor with @Inject. However, for that to work, we need to tell Hilt how to instantiate the type of classes
 * that we will want to inject (i.e., if we want to inject an Engine in the Car class, we need to tell Hilt how to create an Engine). We can
 * create a module annotated with @Module that contains the functions that provide the instances of dependencies that we need, being even able
 * to provide instances of an implementation of a interface.
 */


@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
