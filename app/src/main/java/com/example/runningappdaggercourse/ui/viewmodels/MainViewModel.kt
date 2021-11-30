package com.example.runningappdaggercourse.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.runningappdaggercourse.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**As we don't instantiate ViewModels by directly calling their constructors, it wouldn't work if we just annotated its constructor with @Inject.
 * However, Hilt still provides the @HiltViewModel annotation for ViewModels, which simplify the process of instantiating ViewModels without
 * needing to explicitly handle the ViewModelFactories.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
//class MainViewModel @ViewModelInject constructor(             //Before, to inject dependencies in a ViewModel, we needed to use the @ViewModelInject annotation in the ViewModel's constructor.
    val repository: MainRepository          //Even though we don't have a function that provides MainRepository in our AppModule, Dagger will still check how to create the instance of it and, when it notices that it only needs a RunDAO as its dependency, which the module knows how to create, it will create and inject MainRepository.
): ViewModel() {
}