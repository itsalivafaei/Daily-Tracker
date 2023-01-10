package com.example.dailytracker

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import android.webkit.PermissionRequest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.dailytracker.uiModule.theme.DailyTrackerTheme
import com.example.dailytracker.utilityModule.*

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    /** Permission Checking **/
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            permissionStatus = if (isGranted) {
                Log.i(TAG, "Permission: Granted")
                PermissionResult.GRANTED
            } else {
                Log.i(TAG, "Permission: Denied")
                PermissionResult.DENIED
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


        /** Permission for now usage **/
        try {
            Log.d(TAG, "try block for permission")
            requestPermissionLauncher.launch(Permissions.ACTIVITY_RECOGNITION.value)
        } catch (exception: ActivityNotFoundException) {
            Log.e(TAG, "ActivityNotFoundException")
        }

        /** Handling permission **/
        /*
        val activityRecognitionPermission = finalPermissionRequest(
            applicationContext,
            this,
            Permissions.ACTIVITY_RECOGNITION.value
        )

        when(activityRecognitionPermission) {
            PermissionResult.GRANTED -> {
                Log.d(TAG, "Permission Granted: ${Permissions.ACTIVITY_RECOGNITION.value}")
                // TODO: Update for usage
            }
            PermissionResult.RATIONAL -> {
                Log.d(TAG, "Permission Rational: ${Permissions.ACTIVITY_RECOGNITION.value}")
                // TODO: make Snack bar
            }
            PermissionResult.REQUEST -> {
                Log.d(TAG, "Permission Request: ${Permissions.ACTIVITY_RECOGNITION.value}")
                try {
                    requestPermissionLauncher.launch(Permissions.ACTIVITY_RECOGNITION.value)
                } catch (exception: ActivityNotFoundException) {
                    Log.e(TAG, "ActivityNotFoundException")
                }
                permissionStatus
            }
            PermissionResult.DENIED -> {
                Log.d(TAG, "Permission Denied: ${Permissions.ACTIVITY_RECOGNITION.value}.")
            }
            else -> {
                Log.i(TAG, "else state in when happened.")
            }
        }
         */

        setContent {
            DailyTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(status.toString(), sleep.toString())
                }
            }
        }
    }
}

fun requestPermission() {

}

@Composable
fun Greeting(status: String, sleep: String) {
        Text(
            text = "Play Service availability: $status. and sleep: $sleep",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 24.sp
        )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DailyTrackerTheme {
        Greeting("Default", "Default")
    }
}