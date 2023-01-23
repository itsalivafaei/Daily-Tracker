package mobsensing.edu.dreamy.receiver.activityrecognition

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.ActivityTransitionResult
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.data.activityRecognition.ActivityRecognitionRepository
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityTransitionRecord
import mobsensing.edu.dreamy.data.activityRecognition.db.asRecord

class ActivityRecognitionReceiver : BroadcastReceiver() {
    private val scope = MainScope()

    companion object {
        const val TAG = "DetectedActivityReceiver"

        @RequiresApi(Build.VERSION_CODES.S)
        @SuppressLint("UnspecifiedImmutableFlag")
        fun createActivityRecognitionReceiverPendingIntent(context: Context): PendingIntent {
            val activityRecognitionIntent = Intent(context, ActivityRecognitionReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                0,
                activityRecognitionIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action: ${intent.action}")

        val repository =
            (context.applicationContext as MainApplication).activityRecognitionRepository

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)!!

            if (result.transitionEvents.isNotEmpty()) {
                Log.d(TAG, "transitionEvents: ${ result.transitionEvents }")
                val transitionEvents: List<ActivityTransitionRecord> =
                    result.transitionEvents.map { event ->
                        event.asRecord()
                    }
                addTransitionEventsToDatabase(repository, transitionEvents)
            } else {
                Log.d(TAG,"intent:$intent, ActivityTransitionResult -> is empty.")
            }
        }
    }

    private fun addTransitionEventsToDatabase(
        repository: ActivityRecognitionRepository,
        records: List<ActivityTransitionRecord>
    ) {
        if (records.isNotEmpty()) {
            scope.launch {
                repository.insertAllTransitionRecords(records)
            }
        }
    }
}
