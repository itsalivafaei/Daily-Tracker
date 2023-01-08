package com.example.dailytracker.dataModule.sleep.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.location.SleepSegmentEvent

/*
 * Data class for Sleep Segment Events which represents the time the user slept at night.
 */
@Entity(tableName = "sleep_segment_events_table")
data class SleepSegmentEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "start_time_millis")
    val startTimeMillis: Long,

    @ColumnInfo(name = "end_time_millis")
    val endTimeMillis: Long,

    @ColumnInfo(name = "status")
    val status: Int,
) {
    companion object {
        fun from(sleepSegmentEvent: SleepSegmentEvent): SleepSegmentEventEntity {
            return SleepSegmentEventEntity(
                startTimeMillis = sleepSegmentEvent.startTimeMillis,
                endTimeMillis = sleepSegmentEvent.endTimeMillis,
                status = sleepSegmentEvent.status
            )
        }
    }
}