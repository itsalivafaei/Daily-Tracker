package mobsensing.edu.dreamy.data.activityRecognition.db

import androidx.room.TypeConverter
import java.time.Instant

class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? =
        value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun toTimestamp(date: Instant?): Long? =
        date?.toEpochMilli()
}