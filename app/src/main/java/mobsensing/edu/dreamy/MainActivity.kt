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

    companion object {
        private const val TAG = "MainActivity"
    }

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
                    DreamyApp(permissionState = permissionState)
                }
            }
        }
    }
}