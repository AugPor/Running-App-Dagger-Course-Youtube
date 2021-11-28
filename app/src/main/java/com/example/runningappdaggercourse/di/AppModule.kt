package com.example.runningappdaggercourse.di

import android.content.Context
import androidx.room.Room
import com.example.runningappdaggercourse.db.RunningDatabase
import com.example.runningappdaggercourse.other.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**We need to have module files that tell how to create certain types involved in dependency injection. In Dagger-Hilt, these modules are
 * usually an object (singleton). To define a module in Dagger and in Hilt, we need to annotate the object with @Module. In Hilt, however, we
 * also need to annotate the modules with @InstallIn, to indicate which Android classes this module will be applied to (the instances of
 * dependencies that we generate in the module will be available in all objects of the class the module was installed in).
 */
@Module                                     //Indicates that this is a Dagger-Hilt module, which tells how to generate some dependencies.
@InstallIn(SingletonComponent::class)       //Annotation required by Hilt to indicate the class where the module will be installed in, making the dependencies available according to the lifecycle of that class (by installing them in the SingletonComponent, it will be available while the instance of Application class exists). Dagger doesn't have that annotation, so we would need to create the components by ourselves. In the past, we used the ApplicationComponent class to point to the Application class' scope, but it was changed to SingletonComponent in a newer version of Dagger to allow using Hilt in non-Android Gradle modules.
//@InstallIn(ApplicationComponent::class)   //In the past, we used the ApplicationComponent class to point to the Application class' scope, but it was changed to SingletonComponent in a newer version of Dagger to allow using Hilt in non-Android Gradle modules.

//We also have other components to that maintain the dependencies according to Android classes' lifecycle, such as Activities, Fragments and Services.
//@InstallIn(ActivityComponent::class)
//@InstallIn(FragmentComponent::class)
//@InstallIn(ServiceComponent::class)
object AppModule {

    @Singleton                              //To make a function create a singleton, we need to annotate it with @Singleton (needed in this place to avoid Dagger creating a new instance of the database every time it's requested).
    @Provides                               //We use the @Provides annotation to tell Dagger that a function can be used to provide an instance of a class. Through the return type of the function, Dagger will know that what type of object it creates (to be able to inject in other classes), it will know the class' dependencies through the function parameters and also know the instantiation process through the function body.
    fun provideRunningDatabase(
        @ApplicationContext appContext: Context         //As we will need an Application context for creating the Room database, we can tell Dagger to provide it (as Dagger is the one that is going to call this function) by annotating the parameter with @ApplicationContext.
    ) = Room.databaseBuilder(
        appContext, RunningDatabase::class.java, RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) = db.getRunDao()     //As we already defined how to create the RunningDatabase in the function above, Dagger will automatically provide it in the function argument.
}