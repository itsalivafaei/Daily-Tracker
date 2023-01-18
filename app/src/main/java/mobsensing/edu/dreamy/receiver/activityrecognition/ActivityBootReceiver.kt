package mobsensing.edu.dreamy.receiver.activityrecognition

// ? Hilt Version
/*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.data.activityRecognition.datastore.ActivityTransitionUpdateStatus
import mobsensing.edu.dreamy.util.ActivityRecognitionPermission
import mobsensing.edu.dreamy.util.PlayServicesAvailabilityChecker
import mobsensing.edu.dreamy.util.hasPermission
import javax.inject.Inject

@AndroidEntryPoint(SingletonComponent::class)
class ActivityBootReceiver : BroadcastReceiver() {
    @Inject lateinit var preferences: ActivityTransitionUpdateStatus
    @Inject lateinit var playServicesAvailabilityChecker: PlayServicesAvailabilityChecker

    private val scope = MainScope()

    companion object {
        const val TAG = "ActivityBootReceiver"

        val VALID_ACTIONS = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON"
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action: ${intent.action}")

        if (intent.action !in VALID_ACTIONS) return

        scope.launch {
            val turnOn = preferences.activityTransitionUpdateDataFlow.first()
            if (!turnOn) return@launch

            val success = playServicesAvailabilityChecker.isGoogleServicesAvailable() &&
                    context.hasPermission(ActivityRecognitionPermission)
//                    && activityTransitionManager.requestActivityTransitionUpdates()

            if (!success) {
                preferences.updateActivityTransitionData(false)
            }
        }
    }
}
*/

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.data.activityRecognition.ActivityRecognitionRepository
import mobsensing.edu.dreamy.data.activityRecognition.ActivityTransitionManager
import mobsensing.edu.dreamy.util.ActivityRecognitionPermission
import mobsensing.edu.dreamy.util.PlayServicesAvailabilityChecker
import mobsensing.edu.dreamy.util.hasPermission

class ActivityBootReceiver : BroadcastReceiver() {
    private val scope = MainScope()

    companion object {
        const val TAG = "ActivityBootReceiver"

        val VALID_ACTIONS = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON"
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action: ${intent.action}")
        // ? Check it out
        if (intent.action !in VALID_ACTIONS) return

        val repository: ActivityRecognitionRepository =
            (context.applicationContext as MainApplication).activityRecognitionRepository

        scope.launch {
            val activityTransitionUpdateData = repository.activityTransitionUpdateDataFlow.first()
            if (!activityTransitionUpdateData) return@launch

            val manager = ActivityTransitionManager(context)

            val success = playServicesAvailabilityChecker(context) &&
                    context.hasPermission(ActivityRecognitionPermission) &&
                    manager.requestActivityTransitionUpdates()

            if (!success) {
                repository.updateActivityTransition(false)
            }
        }
    }

    private suspend fun playServicesAvailabilityChecker(context: Context): Boolean {
        val playServicesInstance = GoogleApiAvailability.getInstance()

        val playServicesAvailabilityChecker =
            PlayServicesAvailabilityChecker(context, playServicesInstance)

        return playServicesAvailabilityChecker.isGoogleServicesAvailable()
    }
}