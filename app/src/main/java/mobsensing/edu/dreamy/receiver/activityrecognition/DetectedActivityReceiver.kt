package mobsensing.edu.dreamy.receiver.activityrecognition

// ? Hilt Version
/*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransitionResult
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityTransitionDao
import mobsensing.edu.dreamy.data.activityRecognition.db.asRecord
import mobsensing.edu.dreamy.util.HiltBroadcastReceiver
import javax.inject.Inject

@AndroidEntryPoint(SingletonComponent::class)
class DetectedActivityReceiver : BroadcastReceiver() {
    @Inject lateinit var dao: ActivityTransitionDao
    private val scope = MainScope()

    companion object {
        const val TAG = "DetectedActivityReceiver"
    }


    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action: ${intent.action}")

        val result = ActivityTransitionResult.extractResult(intent) ?: return

        if (result.transitionEvents.isNotEmpty()) {
            scope.launch {
                dao.insert(
                    result.transitionEvents.map { activityTransitionEvent ->
                        activityTransitionEvent.asRecord()
                    }
                )
            }
        }
    }
}
*/

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.data.activityRecognition.ActivityRecognitionRepository
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityTransitionDao
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityTransitionRecord
import mobsensing.edu.dreamy.data.activityRecognition.db.asRecord
import javax.inject.Inject

class DetectedActivityReceiver : BroadcastReceiver() {
    private val scope = MainScope()

    companion object {
        const val TAG = "DetectedActivityReceiver"

        // TODO: Could replace with the one in [ActivityTransitionManager]
        @RequiresApi(Build.VERSION_CODES.S)
        @SuppressLint("UnspecifiedImmutableFlag")
        fun createActivityRecognitionReceiverPendingIntent(context: Context): PendingIntent {
            val activityRecognitionIntent = Intent(context, DetectedActivityReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                0,
                activityRecognitionIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action: ${intent.action}")

        val repository =
            (context.applicationContext as MainApplication).activityRecognitionRepository

        val result = ActivityTransitionResult.extractResult(intent) ?: return

        if (result.transitionEvents.isNotEmpty()) {
            Log.d(TAG, "ActivityTransition is not empty.")
            val transitionEvents: List<ActivityTransitionRecord> = result.transitionEvents.map { event ->
                event.asRecord()
            }
//            scope.launch {
//                dao.insert(
//                    result.transitionEvents.map { activityTransitionEvent ->
//                        activityTransitionEvent.asRecord()
//                    }
//                )
//            }
            addTransitionEventsToDatabase(repository, transitionEvents)
        }
    }

    private fun addTransitionEventsToDatabase(
        repository: ActivityRecognitionRepository,
        records: List<ActivityTransitionRecord>
    ) {
        scope.launch {
            repository.insertAllTransitionRecords(records)
        }
    }
}
