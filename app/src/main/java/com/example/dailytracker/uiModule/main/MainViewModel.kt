package com.example.dailytracker.uiModule.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dailytracker.MainApplication
import com.example.dailytracker.dataModule.sleep.SleepRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MainViewModel(
    private val sleepRepository: SleepRepository
    ): ViewModel() {
    
    /** Methods for SleepSubscriptionStatus **/
    //UI states access for various [MainUiSleepState]
    /*
    Flow is set to emits value for when app is on the foreground
      5 seconds stop delay is added to ensure it flows continuously
      for cases such as configuration change
    */
    val mainSleepState: StateFlow<MainUiSleepState> =
        combine(
            sleepRepository.subscribedToSleepDataFlow,
            sleepRepository.allSleepSegmentEvents,
            sleepRepository.allSleepClassifyEvents
        ) {
            subscribed, segments, classifiers ->
            MainUiSleepState(subscribed, segments, classifiers)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiSleepState()
        )
    /** Casting Flow to StateFlow (instead of LiveData) **/
    /*val sleepUiState: StateFlow<MainUiSleepState> =
        sleepRepository.subscribedToSleepDataFlow.map {
            MainUiSleepState(subscribedToSleepData = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiSleepState()
        )*/
    
    fun updateSubscribedToSleepData(subscribed: Boolean) = viewModelScope.launch { 
        sleepRepository.updateSubscribedToSleepData(subscribed)
    }
    
    /** Methods for segments and classifies **/
    //Stop due to the problem with casting flow to StateFlow
    /*val sleepSegmentEvents: StateFlow<List<SleepSegmentEventEntity>> =
        sleepRepository.allSleepSegmentEvents.stateIn(
            scope = viewModelScope,

        )*/

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MainApplication)
                MainViewModel(application.sleepRepository)
            }
        }
    }
}

