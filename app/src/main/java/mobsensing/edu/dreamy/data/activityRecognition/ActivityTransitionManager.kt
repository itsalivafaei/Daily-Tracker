package mobsensing.edu.dreamy.data.activityRecognition

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


@RequiresApi(Build.VERSION_CODES.S)
class ActivityTransitionManager(private val context: Context) {
    private val scope = MainScope()

    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, ActivityRecognitionReceiver::class.java),
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    companion object {
        const val TAG = "ActivityTransitionManager"
    }

    @SuppressLint("MissingPermission")
    suspend fun requestActivityTransitionUpdates(): Boolean {
        val request = ActivityTransitionRequest(
            DetectedTransitionType.values().map {
                ActivityTransition.Builder()
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .setActivityType(it.type)
                    .build()
            }
        )
        Log.d(TAG, "activity request: $request")
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


    @SuppressLint("MissingPermission")
    suspend fun removeActivityTransitionUpdates() {
        if (context.hasPermission(ActivityRecognitionPermission)) {
            ActivityRecognition
                .getClient(context)
                .removeActivityTransitionUpdates(pendingIntent)
                .await()
        }
    }
}
