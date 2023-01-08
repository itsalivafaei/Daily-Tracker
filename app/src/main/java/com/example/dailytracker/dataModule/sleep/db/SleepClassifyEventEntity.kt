package com.example.dailytracker.dataModule.sleep.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.location.SleepClassifyEvent

/*
 * Entity class (table version of the class) for [SleepClassifyEvent] which represents a sleep
 * classification event including the classification timestamp, the sleep confidence, and the
 * supporting data such as device motion and ambient light level. Classification events are
 * reported regularly.
 */
@Entity(tableName = "sleep_classify_events_table")
data class SleepClassifyEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "time_stamp_seconds")
    val timestampSeconds: Int,

    @ColumnInfo(name = "confidence")
    val confidence: Int,
    @ColumnInfo(name = "motion")
    val motion: Int,
    @ColumnInfo(name = "light")
    val light: Int,
) {
    companion object {
        fun from(sleepClassifyEvent: SleepClassifyEvent): SleepClassifyEventEntity {
            return SleepClassifyEventEntity(
                timestampSeconds = (sleepClassifyEvent.timestampMillis / 1000).toInt(),
                confidence = sleepClassifyEvent.confidence,
                motion = sleepClassifyEvent.motion,
                light = sleepClassifyEvent.light
            )
        }
    }
}