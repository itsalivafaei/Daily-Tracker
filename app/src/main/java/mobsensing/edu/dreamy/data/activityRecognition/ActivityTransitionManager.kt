package mobsensing.edu.dreamy.data.activityRecognition

/*
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import mobsensing.edu.dreamy.data.activityRecognition.db.DetectedTransitionType
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import mobsensing.edu.dreamy.receiver.activityrecognition.DetectedActivityReceiver

*/
/**
 * Class which controls registration and unregistration for activity transition updates.
 *//*

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

    */
/**
     * Register for activity transition updates and return whether the call succeeded.
     *//*

*/
/*
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

        // This await() suspends until the task completes. For codebases not using coroutines, you
        // can either
        // - use Tasks.await(task) to block until the task completes
        // - use addOnCompleteListener() to be notified asynchronously when the task completes
        val task =
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            activityRecognitionClient.requestActivityTransitionUpdates(request, pendingIntent)
        task.await()
        return task.isSuccessful
    }
*//*


*/
/*
    suspend fun removeActivityTransitionUpdate() {
        // This await() suspends until the task completes. For codebases not using coroutines, you
        // can either
        // - use Tasks.await(task) to block until the task completes
        // - use addOnCompleteListener() to be notified asynchronously when the task completes
        activityRecognitionClient.requestActivityTransitionUpdates(pendingIntent).await()
    }
*//*

}*/
