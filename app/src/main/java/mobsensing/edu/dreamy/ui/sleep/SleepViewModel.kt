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
import java.util.Calendar

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
/*
            initializer {
                val application = (this[APPLICATION_KEY] as MainApplication)
                MainViewModel(application.sleepRepository)
            }
*/
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



//    private val outputState = MutableStateFlow(Output())

/*
    val uiState = combine(outputState, repositoryState) {output, repo ->
        val segments = if (repo.sleepSegmentEvents.isNotEmpty()) {
            repo.sleepSegmentEvents.joinToString { "\t$it\n" }
        } else { " - " }

        val classifiers = if (repo.sleepClassifyEvents.isNotEmpty()) {
            repo.sleepClassifyEvents.joinToString { "\t$it\n" }
        } else { " - " }

        Output(
            header = if (repo.subscribedToSleepData) {
                val timestamp = Calendar.getInstance().time.toString()
                "Currently subscribed to sleep data:\nSleepClassifyEvents should occur regularly.\n(Last update: $timestamp):\n\n"
            } else {
                "Currently NOT subscribed to sleep data:\n(Information below was saved from previous sessions to the database):\n\n"
            },
            sleepData = "=== Sleep Segment Events ===\n${segments}\n\n" +
                    "=== Sleep Classify Events ===\n${classifiers}",
            buttonText = if (repo.subscribedToSleepData) "Unsubscribe" else "Subscribe"
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Output())
*/

/*
    private val updateState = combine(_reportState, repositoryState) { report, repo ->
        if (report.subscription != repo.subscribedToSleepData) {
            report.subscription = repo.subscribedToSleepData
        }
        if (repo.sleepSegmentEvents.isNotEmpty()) {
            report.segmentEventOutput = repo.sleepSegmentEvents.last().toString()
        }
        if (repo.sleepClassifyEvents.isNotEmpty()) {
            report.classifyEventOutput = repo.sleepClassifyEvents.last().toString()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )
*/

    // * First attempt
/*
    private val repository = RepositoryState(sleepRepository)
    // ? This may not work -> combine repository and create stateFlow
    private val onCollect = viewModelScope.launch {
        repository.subscribedToSleepData.collectLatest { newSubscribedToSleepData ->
            if (subscribedToSleepData != newSubscribedToSleepData) {
                _reportState.update { repo ->
                    repo.copy(subscription = newSubscribedToSleepData)
                }
            }
        }
        repository.sleepSegmentEvents.collectLatest { newSleepSegment ->
            if (newSleepSegment.isNotEmpty()) {
                _reportState.update { repo ->
                    repo.copy(
                        segmentEventOutput = "\t${newSleepSegment.last()}\n"
                    )
                }
            }
        }
        repository.sleepClassifyEvents.collectLatest { newSleepClassify ->
            if (newSleepClassify.isNotEmpty()) {
                _reportState.update { repo ->
                    repo.copy(
                        classifyEventOutput = "\t${newSleepClassify.last()}\n"
                    )
                }
            }
        }
    }
*/

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

/*
    private fun sleepSegmentEventsOutput(list: List<SleepSegmentEventEntity>): String {
        Log.d(TAG, "\"sleepSegmentEventEntities: $list\"")

        return if (list.isNotEmpty()) {
            list.joinToString(separator = "|") {
                "\t$it\n"
            }
        } else {
            " - "
        }.also {
            updateOutput()
        }
    }

    private fun sleepClassifyEventsOutput(list: List<SleepClassifyEventEntity>): String {
        Log.d(TAG, "\"sleepClassifyEventEntities: $list\"")

        return if (list.isNotEmpty()) {
            list.joinToString(separator = "|") {
                "\t$it\n"
            }
        } else {
            " - "
        }.also {
            updateOutput()
        }
    }

    fun updateOutput(): String {
        Log.d(TAG, "updateOutput()")

        val header = if (subscribedToSleepData) {
            val timestamp = Calendar.getInstance().time.toString()
            "Currently subscribed to sleep data:\nSleepClassifyEvents should occur regularly.\n(Last update: $timestamp):\n\n"
        } else {
            "Currently NOT subscribed to sleep data:\n(Information below was saved from previous sessions to the database):\n\n"
        }

        val sleepData =
            "=== Sleep Segment Events ===\n${sleepSegmentEventsOutput(lastSegments)}\n\n" +
                    "=== Sleep Classify Events ===\n${sleepClassifyEventsOutput(lastClassifiers)}"

        return header + sleepData
    }
*/
}

