package mobsensing.edu.dreamy

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import mobsensing.edu.dreamy.ui.activityrecognition.ActivityRecognitionViewModel
import mobsensing.edu.dreamy.ui.sleep.SleepViewModel
import mobsensing.edu.dreamy.ui.theme.DailyTrackerTheme
import mobsensing.edu.dreamy.util.ActivityRecognitionPermissionState

class MainActivity : ComponentActivity() {

    private val sleepViewModel: SleepViewModel by viewModels()
    private val activityRecognitionViewModel: ActivityRecognitionViewModel by viewModels()

    // ? Previous permission check
//    private var activityRecognitionPermissionRequest: PermissionStatus? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    // ? Previous permission check
//    /** Permission Checking **/
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            isGranted: Boolean ->
//            activityRecognitionPermissionRequest = if (isGranted) {
//                Log.i(TAG, "requestPermissionLauncher: Granted")
//                PermissionStatus.GRANTED
//            } else {
//                Log.i(TAG, "requestPermissionLauncher: Denied")
//                PermissionStatus.DENIED
//            }
//        }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // * location-samples permission checker method
        val permissionState = ActivityRecognitionPermissionState(this) {
            if (it.permissionGranted) {
                Log.d(TAG,"activityRecognitionStatus:${activityRecognitionViewModel.activityTransitionUpdateDataFlow.value}")
                // ? Replaced
                // activityRecognitionViewModel.toggleActivityTransitionUpdates()
                activityRecognitionViewModel.toggle(applicationContext)
                Log.d(TAG,"activityRecognitionStatus:${activityRecognitionViewModel.activityTransitionUpdateDataFlow.value}")
                sleepViewModel.toggleRequestSleepData(applicationContext)
            }
        }

        setContent {
            DailyTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Log.d(TAG,"activityRecognitionStatus:${activityRecognitionViewModel.activityTransitionUpdateDataFlow.value}")
                    DreamyApp(permissionState = permissionState)

                    // ? Previous Permission Check
//                    activityRecognitionPermissionRequest?.let {
//                        DreamyApp()
//                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // ? Previous Permission check
//        /** Checking permissions here**/
//        activityRecognitionPermissionRequest = finalPermissionRequest(
//            applicationContext,
//            this,
//            0
//        )
//
//        when (activityRecognitionPermissionRequest) {
//            PermissionStatus.GRANTED -> {
//                Log.d(TAG, "Permission activityRecognition:\t${activityRecognitionPermissionRequest}")
//                // TODO: Update for usage
//            }
//            PermissionStatus.RATIONAL -> {
//                Log.d(TAG, "Permission activityRecognition:\t${activityRecognitionPermissionRequest}")
//                activityRecognitionPermissionRequest = PermissionStatus.RATIONAL
//                // TODO: make Snack bar
//            }
//            PermissionStatus.REQUEST -> {
//                Log.d(TAG, "Permission activityRecognition:\t${activityRecognitionPermissionRequest}")
//                activityRecognitionPermissionRequest = PermissionStatus.REQUEST
//                try {
//                    Log.d(TAG, "try block for permission")
//                    requestPermissionLauncher.launch(permissionsList[0])
//                } catch (exception: ActivityNotFoundException) {
//                    Log.e(TAG, "ActivityNotFoundException")
//                }
//            }
//            PermissionStatus.DENIED -> {
//                Log.d(TAG, "Permission activityRecognition:\t${activityRecognitionPermissionRequest}")
//            }
//            else -> {
//                Log.d(TAG, "BIG TROUBLE! Permission request fall in else condition.")
//            }
//        }
    }
}