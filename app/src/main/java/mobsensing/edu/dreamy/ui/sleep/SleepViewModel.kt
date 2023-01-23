package mobsensing.edu.dreamy.ui.sleep

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.data.sleep.SleepRepository
import mobsensing.edu.dreamy.receiver.sleep.SleepReceiver

class SleepViewModel(private val sleepRepository: SleepRepository): ViewModel() {

    private lateinit var sleepPendingIntent: PendingIntent
    companion object {
        private const val TAG = "MainViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @RequiresApi(Build.VERSION_CODES.S)
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                return SleepViewModel(
                    (application as MainApplication).sleepRepository
                ) as T
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    val repositoryState: StateFlow<RepositoryState> =
        combine(
            sleepRepository.subscribedToSleepDataFlow,
            sleepRepository.allSleepSegmentEvents,
            sleepRepository.allSleepClassifyEvents
        ) { subscribed, segments, classifiers ->
            RepositoryState(
                subscribed,
                segments,
                classifiers
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RepositoryState()
        )

    private fun updateSubscribedToSleepData(subscribed: Boolean) = viewModelScope.launch {
        sleepRepository.updateSubscribedToSleepData(subscribed)
    }

    // ! Should check permission in Compose
    @RequiresApi(Build.VERSION_CODES.S)
    val toggleRequestSleepData: (Context) -> Unit = { applicationContext ->
        sleepPendingIntent =
            SleepReceiver.createSleepReceiverPendingIntent(context = applicationContext)

        if (repositoryState.value.subscribedToSleepData) {
            unsubscribeToSleepSegmentUpdates(applicationContext,sleepPendingIntent)
        } else {
            subscribeToSleepSegmentUpdates(applicationContext, sleepPendingIntent)
        }
    }
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
}

