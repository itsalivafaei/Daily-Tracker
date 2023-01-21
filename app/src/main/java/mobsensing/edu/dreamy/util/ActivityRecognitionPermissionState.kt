package mobsensing.edu.dreamy.util

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


/**
 * State holder for activity recognition permission. Properties are implemented as State objects so
 * that they trigger a recomposition when the value changes (if the value is read within a
 * Composable scope). This also implements the behavior for requesting the permission and updating
 * the internal state afterward.
 *
 * This class should be initialized in `onCreate()` of the Activity. Sample usage:
 *
 * ```
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *
 *     val permissionState = ActivityRecognitionPermissionState(this) { state ->
 *         if (state.permissionGranted) {
 *             // Do something requiring activity recognition permission
 *         }
 *     }
 *
 *     setContent {
 *         Button(
 *             onClick = { permissionState.requestPermission() }
 *         ) {
 *             Text("Click Me")
 *         }
 *     }
 * }
 * ```
 */
class ActivityRecognitionPermissionState(
    private val activity: ComponentActivity,
    private val onResult: (ActivityRecognitionPermissionState) -> Unit
) {
    /** Whether permission was granted for activity recognition. */
    var permissionGranted by mutableStateOf(false)
        private set

    /** Whether to show a rationale for permission to use activity recognition. */
    var needsRationale by mutableStateOf(false)
        private set

    /** Whether to show a degraded experience (set after the permission is denied). */
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

    /**
     * Launch the permission request. Note that this may or may not show the permission UI if the
     * permission has already been granted or if the user has denied permission multiple times.
     */
    fun requestPermission() {
        permissionLauncher.launch(ActivityRecognitionPermission)
    }
}




























