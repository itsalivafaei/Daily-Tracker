package mobsensing.edu.dreamy.receiver.sleep

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
//import com.example.dailytracker.MainApplication
//import com.example.dailytracker.data.sleep.SleepRepository
//import com.example.dailytracker.data.sleep.db.SleepClassifyEventEntity
//import com.example.dailytracker.data.sleep.db.SleepSegmentEventEntity
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.MainApplication
import mobsensing.edu.dreamy.data.sleep.SleepRepository
import mobsensing.edu.dreamy.data.sleep.db.SleepClassifyEventEntity
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity
import mobsensing.edu.dreamy.receiver.activityrecognition.ActivityRecognitionReceiver

class SleepReceiver : BroadcastReceiver() {
    // Used to launch coroutines (non-blocking way to insert data).
    private val scope: CoroutineScope = MainScope()

    companion object {
        const val TAG = "SleepReceiver"

        @RequiresApi(Build.VERSION_CODES.S)
        // ! Check this one
//        @SuppressLint("UnspecifiedImmutableFlag")
        fun createSleepReceiverPendingIntent(context: Context): PendingIntent {
            val sleepIntent = Intent(context, SleepReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                0,
                sleepIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive(): $intent")

        val repository =
            (context.applicationContext as MainApplication).sleepRepository

        // TODO: Extract sleep information from PendingIntent.
        if (SleepSegmentEvent.hasEvents(intent)) {
            val sleepSegmentEvents: List<SleepSegmentEvent> =
                SleepSegmentEvent.extractEvents(intent)
            Log.d(TAG, "SleepSegmentEvent List: $sleepSegmentEvents")
            addSleepSegmentEventsToDatabase(repository, sleepSegmentEvents)
        } else if (SleepClassifyEvent.hasEvents(intent)) {
            val sleepClassifyEvents: List<SleepClassifyEvent> =
                SleepClassifyEvent.extractEvents(intent)
            Log.d(TAG, "SleepClassifyEvent List: $sleepClassifyEvents")
            addSleepClassifyEventsToDatabase(repository, sleepClassifyEvents)
        } else {
            Log.d(TAG,"intent:$intent, SleepSegmentEvent && SleepClassifyEvent -> are empty.")

        }
    }

    private fun addSleepSegmentEventsToDatabase(
        repository: SleepRepository,
        sleepSegmentEvents: List<SleepSegmentEvent>
    ) {
        if (sleepSegmentEvents.isNotEmpty()) {
            scope.launch {
                val convertedToEntityVersion: List<SleepSegmentEventEntity> =
                    sleepSegmentEvents.map {
                        SleepSegmentEventEntity.from(it)
                    }
                repository.insertSleepSegments(convertedToEntityVersion)
            }
        }
    }

    private fun addSleepClassifyEventsToDatabase(
        repository: SleepRepository,
        sleepClassifyEvents: List<SleepClassifyEvent>
    ) {
        if (sleepClassifyEvents.isNotEmpty()) {
            scope.launch {
                val convertedToEntityVersion: List<SleepClassifyEventEntity> =
                    sleepClassifyEvents.map {
                        SleepClassifyEventEntity.from(it)
                    }
                repository.insertSleepClassifyEvents(convertedToEntityVersion)
            }
        }
    }
}