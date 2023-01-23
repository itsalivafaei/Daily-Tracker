package mobsensing.edu.dreamy.data.activityRecognition.db

import android.os.SystemClock
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.DetectedActivity
import java.time.Duration
import java.time.Instant

@Entity(tableName = "activity_transition_records_table")
@TypeConverters(TimestampConverter::class)
data class ActivityTransitionRecord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "activity_type")
    val activityType: DetectedActivityType,

    @ColumnInfo(name = "transition_type")
    val transitionType: DetectedTransitionType,

    @ColumnInfo(name = "timestamp")
    val timestamp: Instant
)

enum class DetectedActivityType(val type: Int) {
    IN_VEHICLE(DetectedActivity.IN_VEHICLE),
    ON_BICYCLE(DetectedActivity.ON_BICYCLE),
    ON_FOOT(DetectedActivity.ON_FOOT),
    RUNNING(DetectedActivity.RUNNING),
    STILL(DetectedActivity.STILL),
    TILTING(DetectedActivity.TILTING),
    UNKNOWN(DetectedActivity.UNKNOWN),
    WALKING(DetectedActivity.WALKING);

    companion object {
        fun forType(type: Int): DetectedActivityType =
            values().first { it.type == type }
    }
}

enum class DetectedTransitionType(val type: Int) {
    ENTER(ActivityTransition.ACTIVITY_TRANSITION_ENTER),
    EXIT(ActivityTransition.ACTIVITY_TRANSITION_EXIT);

    companion object {
        fun forType(type: Int): DetectedTransitionType =
            values().first { it.type == type }
    }
}


fun ActivityTransitionEvent.asRecord() = ActivityTransitionRecord(
    activityType = DetectedActivityType.forType(activityType),
    transitionType = DetectedTransitionType.forType(transitionType),
    timestamp = elapsedRealTimeNanosToInstant(elapsedRealTimeNanos)
)

fun elapsedRealTimeNanosToInstant(elapsedRealTimeNanos: Long): Instant {
    val currentElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
    val currentInstant = Instant.now()

    return currentInstant - Duration.ofNanos(currentElapsedRealtimeNanos - elapsedRealTimeNanos)
}
