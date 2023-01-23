package mobsensing.edu.dreamy.util

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


class ActivityRecognitionPermissionState(
    private val activity: ComponentActivity,
    private val onResult: (ActivityRecognitionPermissionState) -> Unit
) {
    var permissionGranted by mutableStateOf(false)
        private set

    var needsRationale by mutableStateOf(false)
        private set

    var showDegradedExperience by mutableStateOf(false)
        private set

    private val permissionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            updateState()
            showDegradedExperience = !granted
            onResult(this)
        }

    init {
        updateState()
    }

    private fun updateState() {
        permissionGranted = activity.hasPermission(ActivityRecognitionPermission)
        needsRationale = activity.shouldShowRequestPermissionRationale(ActivityRecognitionPermission)
    }

    fun requestPermission() {
        permissionLauncher.launch(ActivityRecognitionPermission)
    }
}




























