package mobsensing.edu.dreamy.receiver.sleep

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.data.sleep.SleepRepository

class SleepBootReceiver : BroadcastReceiver() {
    private val scope: CoroutineScope = MainScope()

    companion object {
        const val TAG = "SleepBootReceiver"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action: ${intent.action}")

        val repository: SleepRepository =
            (context.applicationContext as MainApplication).sleepRepository

        scope.launch {
            val subscribedToSleepData = repository.subscribedToSleepDataFlow.first()
            if (subscribedToSleepData) {
                subscribeToSleepSegmentUpdates(
                    context = context,
                    pendingIntent = SleepReceiver.createSleepReceiverPendingIntent(context)
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun subscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "subscribeToSleepSegmentUpdates()")

        if (activityRecognitionPermissionApproved(context)) {
            val task =
                ActivityRecognition.getClient(context).requestSleepSegmentUpdates(
                    pendingIntent,
                    SleepSegmentRequest.getDefaultSleepSegmentRequest()
                )

            task.addOnSuccessListener {
                Log.d(TAG, "Successfully subscribed to sleep data from boot.")
            }
            task.addOnFailureListener { exception ->
                Log.d(TAG, "Exception when subscribing to sleep data from boot: $exception")

                val repository =
                    (context.applicationContext as MainApplication).sleepRepository
                unsubscribeStatusForSleepData(repository)
            }
        } else {
            Log.d(TAG, "Failed to subscribed to sleep data from boot; Permission removed.")

            val repository =
                (context.applicationContext as MainApplication).sleepRepository
            unsubscribeStatusForSleepData(repository)
        }
    }

    private fun unsubscribeStatusForSleepData(repository: SleepRepository) = scope.launch {
        repository.updateSubscribedToSleepData(false)
    }

    private fun activityRecognitionPermissionApproved(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACTIVITY_RECOGNITION
        )
    }
}