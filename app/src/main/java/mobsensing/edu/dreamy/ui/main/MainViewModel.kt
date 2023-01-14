package mobsensing.edu.dreamy.ui.main

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
//import com.example.dailytracker.MainApplication
//import com.example.dailytracker.data.sleep.SleepRepository
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.data.sleep.SleepRepository
import mobsensing.edu.dreamy.data.sleep.db.SleepClassifyEventEntity
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity
import java.util.Calendar

/*
enum class Subscription {
    SUBSCRIBED,
    UNSUBSCRIBED
}
*/
class MainViewModel(private val sleepRepository: SleepRepository): ViewModel() {

    private lateinit var sleepPendingIntent: PendingIntent
    companion object {
        private const val TAG = "MainViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MainApplication)
                MainViewModel(application.sleepRepository)
            }
        }
    }


    /** Methods for SleepSubscriptionStatus **/
    //UI states access for various [MainUiSleepState]
    /*
    Flow is set to emits value for when app is on the foreground
      5 seconds stop delay is added to ensure it flows continuously
      for cases such as configuration change
    */
    /**Combine all states*/
    private val mainSleepState: StateFlow<SleepState> =
        combine(
            sleepRepository.subscribedToSleepDataFlow,
            sleepRepository.allSleepSegmentEvents,
            sleepRepository.allSleepClassifyEvents
        ) {
            subscribed, segments, classifiers ->
            SleepState(subscribed, segments, classifiers)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SleepState()
        )

    private fun updateSubscribedToSleepData(subscribed: Boolean) = viewModelScope.launch {
        sleepRepository.updateSubscribedToSleepData(subscribed)
    }

    // =============================================================================================
    // ? Mine
//    private val permission = permissions["ACTIVITY_RECOGNITION"]
    var subscriptionButtonText = "Subscribe"

    // ! TODO: CHECK THIS TO SEE IF WORKS CORRECT
/*
    var observe = false
    private val isStateChange by Delegates.observable(mainSleepState) { property, oldValue, newValue ->
        updateOutput()
    }
    private fun test() {Log.d(TAG, "val isStateChange($observe): $isStateChange")}
*/

    // Collect mainstates data to check if any changes happened
    private var newSleepSegment: Boolean = false
    private var newSleepClassify: Boolean = false
    private var lastSleepSegment: SleepSegmentEventEntity? = null
        set(value) {
            newSleepSegment = field != value
            field = value
        }
    private var lastSleepClassify: SleepClassifyEventEntity? = null
        set(value) {
            newSleepClassify = field != value
            field = value
        }


    private val collectMainSleepStateChanges = viewModelScope.launch {
        mainSleepState.collectLatest { state ->
            if (subscribedToSleepData != state.subscribedToSleepData)
                subscribedToSleepData = state.subscribedToSleepData

            if (state.sleepSegmentEvents.isNotEmpty()) {
                lastSleepSegment = state.sleepSegmentEvents.last()
                if (newSleepSegment)
                    sleepSegmentEventsOutput()
            }

            if (state.sleepClassifyEvents.isNotEmpty()) {
                lastSleepClassify = state.sleepClassifyEvents.last()
                if (newSleepClassify)
                    sleepClassifyEventsOutput()
            }
        }
    }

    // =============================================================================================
    // ? This part was implemented in MainActivity.kt

    private var subscribedToSleepData = false
        set(newSubscribedToSleepData) {
            field = newSubscribedToSleepData
            subscriptionButtonText = if (newSubscribedToSleepData) {
                Log.d(TAG, "Unsubscribe to sleep data")
                "Unsubscribe"
            } else {
                Log.d(TAG, "Subscribe to sleep data")
                "Subscribe"
            }
            updateOutput()
        }

    /** Enable/Disable sleep tracking and ask for permissions if needed. **/
    val onClickRequestSleepData: (Context) -> Unit = { applicationContext ->
        if (subscribedToSleepData) {
            unsubscribeToSleepSegmentUpdates(applicationContext,sleepPendingIntent)
        } else {
            subscribeToSleepSegmentUpdates(applicationContext, sleepPendingIntent)
        }
    }


    /** Permission is checked before this method is called. **/
    @SuppressLint("MissingPermission")
    private fun subscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "requestSleepSegmentUpdates()")

        // ? Request Sleep API updates
        val task = ActivityRecognition.getClient(context).requestSleepSegmentUpdates(
            pendingIntent,
            // Registers for both [SleepSegmentEvent] and [SleepClassifyEvent] data.
            SleepSegmentRequest.getDefaultSleepSegmentRequest()
        )

        task.addOnSuccessListener {
            updateSubscribedToSleepData(true)
            Log.d(TAG, "Successfully subscribed to sleep data.")
        }

        task.addOnFailureListener { exception ->
            Log.d(TAG, "Exception when subscribing to sleep data: $exception")
        }
    }

    private fun unsubscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "unsubscribeToSleepSegmentUpdates()")

        val task = ActivityRecognition.getClient(context).removeSleepSegmentUpdates(pendingIntent)

        task.addOnSuccessListener {
            updateSubscribedToSleepData(false)
            Log.d(TAG, "Successfully unsubscribed to sleep data.")
        }

        task.addOnFailureListener { exception ->
            Log.d(TAG, "Exception when unsubscribing to sleep data: $exception")
        }
    }


     // ? Data is saved to the database via [SleepReceiver] and when that data changes,
     // ? we get notified of changes.
     // ! Note: The data returned is Entity versions of the sleep classes, so they don't contain
     // ! all the data, as I just saved the minimum to show it's being saved.
    private fun sleepSegmentEventsOutput(): String {
        val sleepSegmentEventEntities = mainSleepState.value.sleepSegmentEvents
        Log.d(TAG, "\"sleepSegmentEventEntities: $sleepSegmentEventEntities\"")

        return if (sleepSegmentEventEntities.isNotEmpty()) {
            sleepSegmentEventEntities.joinToString {
                "\t$it\n"
            }
        } else {
            " - "
        }.also {
                updateOutput()
        }
    }

    private fun sleepClassifyEventsOutput(): String {
        val sleepClassifyEventEntities = mainSleepState.value.sleepSegmentEvents
        Log.d(TAG, "\"sleepClassifyEventEntities: $sleepClassifyEventEntities\"")

        return if (sleepClassifyEventEntities.isNotEmpty()) {
            sleepClassifyEventEntities.joinToString {
                "\t$it\n"
            }
        } else {
            " - "
        }.also {
            updateOutput()
        }
    }

    /**
     * Rudimentary implementation of the output from multiple tables. The LiveData observers
     * ? (IN OUR CASE, STATEFLOW)
     * just save their data to one of the strings (segmentOutput or classifyOutput)
     * ! and triggers this function.
     */
    fun updateOutput(): String {
        Log.d(TAG, "updateOutput()")

        val header = if (subscribedToSleepData) {
            val timestamp = Calendar.getInstance().time.toString()
            "Currently subscribed to sleep data:\nSleepClassifyEvents should occur regularly.\n(Last update: $timestamp):\n\n"
        } else {
            "Currently NOT subscribed to sleep data:\n(Information below was saved from previous sessions to the database):\n\n"
        }

        val sleepData =
            "=== Sleep Segment Events ===\n${sleepSegmentEventsOutput()}\n\n" +
                    "=== Sleep Classify Events ===\n${sleepClassifyEventsOutput()}"

        return header + sleepData
    }
}


/** Casting Flow to StateFlow (instead of LiveData) **/
/*val sleepUiState: StateFlow<MainUiSleepState> =
    sleepRepository.subscribedToSleepDataFlow.map {
        MainUiSleepState(subscribedToSleepData = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainUiSleepState()
    )*/



/** Methods for segments and classifies **/
//Stop due to the problem with casting flow to StateFlow
/*val sleepSegmentEvents: StateFlow<List<SleepSegmentEventEntity>> =
    sleepRepository.allSleepSegmentEvents.stateIn(
        scope = viewModelScope,

    )*/

/**Use Observable on Stateflow*/
/*
    val myState: StateFlow<MainUiSleepState> by Delegates.observable(
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
    ) {property, oldValue, newValue ->
        updateOutput()
        Log.d("myState", "Data: $myState")
    }
*/

// ! Use the one in MainUtil!
/** Review Activity Recognition permission checking. **/
/*
private fun activityRecognitionPermissionApproved(context: Context): Boolean {
    return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
        // ? context = this -> MainActivity
        context,
        android.Manifest.permission.ACTIVITY_RECOGNITION
    )
}
*/
