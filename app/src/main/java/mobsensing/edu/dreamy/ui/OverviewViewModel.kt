package mobsensing.edu.dreamy.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.ui.activityrecognition.ActivityRecognitionViewModel
import mobsensing.edu.dreamy.util.ActivityRecognitionPermissionState
import mobsensing.edu.dreamy.util.PlayServicesAvailabilityChecker

class OverviewViewModel(
    private val playServicesState: PlayServicesAvailabilityChecker
) : ViewModel() {

    companion object {
        private const val TAG = "ActivityRecognitionViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @RequiresApi(Build.VERSION_CODES.S)
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()

                return OverviewViewModel(
                    (application as MainApplication).playServicesState
                ) as T
            }
        }
    }

    val playServicesAvailableState = flow {
        emit(
            if (playServicesState.isGoogleServicesAvailable()) {
                PlayServicesAvailableState.PlayServicesAvailable
            } else {
                PlayServicesAvailableState.PlayServicesUnavailable
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PlayServicesAvailableState.Initializing)



}

enum class PlayServicesAvailableState {
    Initializing, PlayServicesAvailable, PlayServicesUnavailable
}