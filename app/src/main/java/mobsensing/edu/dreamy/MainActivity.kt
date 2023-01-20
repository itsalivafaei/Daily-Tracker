package mobsensing.edu.dreamy

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import mobsensing.edu.dreamy.ui.main.SleepScreen
import mobsensing.edu.dreamy.ui.theme.DailyTrackerTheme
import mobsensing.edu.dreamy.util.PermissionStatus
import mobsensing.edu.dreamy.util.finalPermissionRequest
import mobsensing.edu.dreamy.util.permissionsList

class MainActivity : ComponentActivity() {

    private var activityRecognitionPermissionRequest: PermissionStatus? = null

    companion object {
        private const val TAG = "MainActivity"
    }

    /** Permission Checking **/
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            activityRecognitionPermissionRequest = if (isGranted) {
                Log.i(TAG, "requestPermissionLauncher: Granted")
                PermissionStatus.GRANTED
            } else {
                Log.i(TAG, "requestPermissionLauncher: Denied")
                PermissionStatus.DENIED
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    activityRecognitionPermissionRequest?.let {
/*
                        SleepScreen(
                            applicationContext = applicationContext,
                            sleepPermission = it.code
                        )
*/
                        DreamyApp()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        /** Checking permissions here**/
        activityRecognitionPermissionRequest = finalPermissionRequest(
            applicationContext,
            this,
            0
        )

        when (activityRecognitionPermissionRequest) {
            PermissionStatus.GRANTED -> {
                Log.d(TAG, "Permission activityRecognition:\t${activityRecognitionPermissionRequest}")
                // TODO: Update for usage
            }
            PermissionStatus.RATIONAL -> {
                Log.d(TAG, "Permission activityRecognition:\t${activityRecognitionPermissionRequest}")
                activityRecognitionPermissionRequest = PermissionStatus.RATIONAL
                // TODO: make Snack bar
            }
            PermissionStatus.REQUEST -> {
                Log.d(TAG, "Permission activityRecognition:\t${activityRecognitionPermissionRequest}")
                activityRecognitionPermissionRequest = PermissionStatus.REQUEST
                try {
                    Log.d(TAG, "try block for permission")
                    requestPermissionLauncher.launch(permissionsList[0])
                } catch (exception: ActivityNotFoundException) {
                    Log.e(TAG, "ActivityNotFoundException")
                }
            }
            PermissionStatus.DENIED -> {
                Log.d(TAG, "Permission activityRecognition:\t${activityRecognitionPermissionRequest}")
            }
            else -> {
                Log.d(TAG, "BIG TROUBLE! Permission request fall in else condition.")
            }
        }
//        while (activityRecognitionPermissionRequest != PermissionStatus.GRANTED ||
//            activityRecognitionPermissionRequest != PermissionStatus.DENIED)
    }
}

@Composable
fun Greeting() {
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DailyTrackerTheme {
        Greeting()
    }
}