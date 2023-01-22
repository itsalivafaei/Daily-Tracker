package mobsensing.edu.dreamy.data.activityRecognition

// ? Hilt Version
/*
@RequiresApi(Build.VERSION_CODES.S)
class ActivityTransitionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityRecognitionClient: ActivityRecognitionClient
) {

    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, DetectedActivityReceiver::class.java),
            // Note: must use FLAG_MUTABLE in order for Play Services to add the result data to the
            // intent starting in API level 31. Otherwise the BroadcastReceiver will be started but
            // the Intent will have no data.
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    companion object {
        const val TAG = "ActivityTransitionManager"
    }

//     Register for activity transition updates and return whether the call succeeded.

    private fun createRequest() {

    }
    @SuppressLint("MissingPermission")
    suspend fun requestActivityTransitionUpdates(context: Context): Boolean {
        // Real apps will want transitions that make sense for a particular feature. For example,
        // an app that changes its behavior while the user is driving a vehicle will want two
        // transitions:
        //   - DetectedActivity.IN_VEHICLE with ActivityTransition.ACTIVITY_TRANSITION_ENTER
        //   - DetectedActivity.IN_VEHICLE with ActivityTransition.ACTIVITY_TRANSITION_EXIT
        //
        // This sample will show the most recent transitions of any type, so request updates for all
        // DetectedActivity types. We can request just ActivityTransition.ACTIVITY_TRANSITION_ENTER
        // transitions, because entering a new activity type implies exiting the old one.
        val request = ActivityTransitionRequest(
            DetectedTransitionType.values().map {
                ActivityTransition.Builder()
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .setActivityType(it.type)
                    .build()
            }
        )

        // This await() suspends until the task completes. For codebases not using coroutines, you
        // can either
        // - use Tasks.await(task) to block until the task completes
        // - use addOnCompleteListener() to be notified asynchronously when the task completes
        var successfullyRequest = false

        if (activityRecognitionPermissionApproved(context)) {
            val task =
                activityRecognitionClient.requestActivityTransitionUpdates(request, pendingIntent)

             task.addOnSuccessListener {
                 Log.d(TAG, "Successfully request for activity transition updates.")
                 successfullyRequest = true
             }

            task.addOnFailureListener { exception ->
                Log.d(TAG, "Exception when subscribing to sleep data from boot: $exception")
                successfullyRequest = false

                // ? My code
                // TODO: remove activity transition updates (like Sleep BootReceiver)
            }
        } else {
            Log.d(TAG, "Failed to request for transition update form transition manager; Permission removed.")
            successfullyRequest = false
        }

        return successfullyRequest
    }

    // ! check for addonSuccess and addOnFailure for remove method
    // ! resolve the context
    @SuppressLint("MissingPermission")
    suspend fun removeActivityTransitionUpdates(context: Context) {
        // This await() suspends until the task completes. For codebases not using coroutines, you
        // can either
        // - use Tasks.await(task) to block until the task completes
        // - use addOnCompleteListener() to be notified asynchronously when the task completes
        if (activityRecognitionPermissionApproved(context)) {
            activityRecognitionClient.removeActivityTransitionUpdates(pendingIntent).await()
        }
    }


    private fun activityRecognitionPermissionApproved(context: Context): Boolean {
        // Because this app targets 29 and above (recommendation for using the Sleep APIs), we
        // don't need to check if this is on a device before runtime permissions, that is, a device
        // prior to 29 / Q.
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACTIVITY_RECOGNITION
        )
    }
}
*/

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.tasks.await
import mobsensing.edu.dreamy.data.activityRecognition.db.DetectedTransitionType
import mobsensing.edu.dreamy.receiver.activityrecognition.ActivityRecognitionReceiver
import mobsensing.edu.dreamy.util.ActivityRecognitionPermission
import mobsensing.edu.dreamy.util.hasPermission


//Class which controls registration and unregistration for activity transition updates.
@RequiresApi(Build.VERSION_CODES.S)
class ActivityTransitionManager(private val context: Context) {
    private val scope = MainScope()

    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, ActivityRecognitionReceiver::class.java),
            // Note: must use FLAG_MUTABLE in order for Play Services to add the result data to the
            // intent starting in API level 31. Otherwise the BroadcastReceiver will be started but
            // the Intent will have no data.
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    companion object {
        const val TAG = "ActivityTransitionManager"
    }

//     Register for activity transition updates and return whether the call succeeded.
    @SuppressLint("MissingPermission")
    suspend fun requestActivityTransitionUpdates(): Boolean {
        // Real apps will want transitions that make sense for a particular feature. For example,
        // an app that changes its behavior while the user is driving a vehicle will want two
        // transitions:
        //   - DetectedActivity.IN_VEHICLE with ActivityTransition.ACTIVITY_TRANSITION_ENTER
        //   - DetectedActivity.IN_VEHICLE with ActivityTransition.ACTIVITY_TRANSITION_EXIT
        //
        // This sample will show the most recent transitions of any type, so request updates for all
        // DetectedActivity types. We can request just ActivityTransition.ACTIVITY_TRANSITION_ENTER
        // transitions, because entering a new activity type implies exiting the old one.
        val request = ActivityTransitionRequest(
            DetectedTransitionType.values().map {
                ActivityTransition.Builder()
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .setActivityType(it.type)
                    .build()
            }
        )
        Log.d(TAG, "activity request: $request")

        // This await() suspends until the task completes. For codebases not using coroutines, you
        // can either
        // - use Tasks.await(task) to block until the task completes
        // - use addOnCompleteListener() to be notified asynchronously when the task completes
        var successfullyRequest = false

        val task =
            ActivityRecognition
                .getClient(context)
                .requestActivityTransitionUpdates(request, pendingIntent)

         task.addOnSuccessListener {
             Log.d(TAG, "Successfully request for activity transition updates.")
             successfullyRequest = true
         }

        task.addOnFailureListener { exception ->
            Log.d(TAG, "Exception when subscribing to sleep data from boot: $exception")
            successfullyRequest = false

            // ? My code
            // TODO: remove activity transition updates (like Sleep BootReceiver)
        }
        return successfullyRequest
    }



    // ! check for addonSuccess and addOnFailure for remove method
    // ! resolve the context
    @SuppressLint("MissingPermission")
    suspend fun removeActivityTransitionUpdates() {
        // This await() suspends until the task completes. For codebases not using coroutines, you
        // can either
        // - use Tasks.await(task) to block until the task completes
        // - use addOnCompleteListener() to be notified asynchronously when the task completes
        if (context.hasPermission(ActivityRecognitionPermission)) {
            ActivityRecognition
                .getClient(context)
                .removeActivityTransitionUpdates(pendingIntent)
                .await()
        }
    }
}
