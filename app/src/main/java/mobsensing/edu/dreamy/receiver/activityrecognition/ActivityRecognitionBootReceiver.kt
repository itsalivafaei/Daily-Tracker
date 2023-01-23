package mobsensing.edu.dreamy.receiver.activityrecognition

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.data.activityRecognition.ActivityRecognitionRepository
import mobsensing.edu.dreamy.data.activityRecognition.db.DetectedTransitionType
import mobsensing.edu.dreamy.util.ActivityRecognitionPermission
import mobsensing.edu.dreamy.util.PlayServicesAvailabilityChecker
import mobsensing.edu.dreamy.util.hasPermission

val requestsList = ActivityTransitionRequest(
    DetectedTransitionType.values().map {
        ActivityTransition
            .Builder()
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .setActivityType(it.type)
            .build()
    }
)

class ActivityRecognitionBootReceiver : BroadcastReceiver() {
    private val scope = MainScope()

    companion object {
        const val TAG = "ActivityRecognitionBootReceiver"

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
        if (intent.action !in VALID_ACTIONS) return

        val repository: ActivityRecognitionRepository =
            (context.applicationContext as MainApplication).activityRecognitionRepository

        scope.launch {
            val activityTransitionUpdateData =
                repository.activityTransitionUpdateDataFlow.first()

            if (activityTransitionUpdateData) {
                if (playServicesAvailabilityChecker(context)) {
                    requestActivityTransitionUpdates(
                        context = context,
                        pendingIntent = ActivityRecognitionReceiver
                            .createActivityRecognitionReceiverPendingIntent(context)
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun requestActivityTransitionUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG,"registerForActivityTransitionUpdates()")

        if (context.hasPermission(ActivityRecognitionPermission)) {
            val task =
                ActivityRecognition
                    .getClient(context)
                    .requestActivityTransitionUpdates(requestsList, pendingIntent)

            task.addOnSuccessListener {
                Log.d(TAG, "Successfully register for activity transition updates from boot.")
            }

            task.addOnFailureListener { exception ->
                Log.d(TAG, "Exception when registering for activity transition updates form boot: $exception")

                val repository =
                    (context.applicationContext as MainApplication).activityRecognitionRepository
                removeActivityTransitionUpdates(repository)
            }
        } else {
            Log.d(TAG, "Failed to register for activity recognition updates from boot; Permission removed.")

            val repository =
                (context.applicationContext as MainApplication).activityRecognitionRepository
            removeActivityTransitionUpdates(repository)
        }
    }

    private fun removeActivityTransitionUpdates(repository: ActivityRecognitionRepository) = scope.launch {
        repository.updateActivityTransition(false)
    }

    private suspend fun playServicesAvailabilityChecker(context: Context): Boolean {
        val playServicesInstance = GoogleApiAvailability.getInstance()

        val playServicesAvailabilityChecker =
            PlayServicesAvailabilityChecker(context, playServicesInstance)

        return playServicesAvailabilityChecker.isGoogleServicesAvailable()
    }
}