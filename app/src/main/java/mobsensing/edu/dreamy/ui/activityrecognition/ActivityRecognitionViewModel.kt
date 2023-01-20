package mobsensing.edu.dreamy.ui.activityrecognition

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.R
import mobsensing.edu.dreamy.data.activityRecognition.ActivityRecognitionRepository
import mobsensing.edu.dreamy.data.activityRecognition.ActivityTransitionManager
import mobsensing.edu.dreamy.util.PlayServicesAvailabilityChecker

/**
 * View model for the main screen of the app (which also happens to be the only screen). This stores
 * state relevant to the UI so that state is properly maintained across configuration changes.
 */
class ActivityRecognitionViewModel(
    private val repository: ActivityRecognitionRepository
) : ViewModel() {

    private lateinit var activityRecognitionPendingIntent: PendingIntent
    private val scope = viewModelScope

    companion object {
        private const val TAG = "ActivityRecognitionViewModel"
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()

                return ActivityRecognitionViewModel(
                    (application as MainApplication).activityRecognitionRepository
                ) as T
            }
        }
    }

    // ! Check this
/*
    val playServicesAvailableState = flow {
        emit(
            if (playServicesAvailabilityChecker(context = ))
        )
    }
*/

    val activityTransitionUpdateDataFlow = repository.activityTransitionUpdateDataFlow
        .stateIn(scope, SharingStarted.Eagerly, false)

    // ? This is the most recent
    val mostRecentTransitionEvents = repository.getMostRecentTransitions
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val lastTransitionEvent = repository.getLastTransition
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var errorMessages by mutableStateOf(emptyList<ErrorMessage>())
        private set

    private suspend fun playServicesAvailabilityChecker(context: Context): Boolean {
        val playServicesInstance = GoogleApiAvailability.getInstance()

        val playServicesAvailabilityChecker =
            PlayServicesAvailabilityChecker(context, playServicesInstance)

        return playServicesAvailabilityChecker.isGoogleServicesAvailable()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun toggleActivityTransitionUpdates(context: Context) {
        val manager = ActivityTransitionManager(context)
        if (activityTransitionUpdateDataFlow.value) {
            stopActivityTransitionUpdates(manager)
        } else {
            startActivityTransitionUpdates(manager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startActivityTransitionUpdates(manager: ActivityTransitionManager) {
        scope.launch {
            if (manager.requestActivityTransitionUpdates()) {
                repository.updateActivityTransition(true)
            } else {
                errorMessages = errorMessages + ErrorMessage(R.string.error_requesting_updates)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun stopActivityTransitionUpdates(manager: ActivityTransitionManager) {
        scope.launch {
            manager.removeActivityTransitionUpdates()
            repository.updateActivityTransition(false)
            repository.deleteAllTransitionRecords()
        }
    }

    fun removeMessage(errorMessage: ErrorMessage) {
        errorMessages = errorMessages.filterNot { it == errorMessage }
    }
}
