package mobsensing.edu.dreamy

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import mobsensing.edu.dreamy.ui.main.MainScreen
import mobsensing.edu.dreamy.ui.main.MainUi
//import com.example.dailytracker.ui.theme.DailyTrackerTheme
//import com.example.dailytracker.util.*
import mobsensing.edu.dreamy.ui.theme.DailyTrackerTheme
import mobsensing.edu.dreamy.util.*
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

        val status: Boolean
        val sleep: Boolean
        //Granting permissions
        GoogleServices(this).let {
            status = it.googlePlayServiceAvailabilityCheck()
            sleep = it.activityApproved()
        }


        setContent {
            DailyTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    activityRecognitionPermissionRequest?.let {
                        MainUi(
                            applicationContext = applicationContext,
                            sleepPermission = it.code
                        )
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
fun Greeting(status: String, sleep: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Play Service availability: $status. and sleep: $sleep",
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 24.sp
        )
        Text(
            text = stringResource(R.string.long_text),
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DailyTrackerTheme {
        Greeting("Default", "Default")
    }
}