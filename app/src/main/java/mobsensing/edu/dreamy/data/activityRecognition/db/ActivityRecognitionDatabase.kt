package mobsensing.edu.dreamy.data.activityRecognition.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ActivityTransitionRecord::class], version = 1, exportSchema = false)
abstract class ActivityRecognitionDatabase : RoomDatabase() {
    abstract fun getActivityTransitionRecordDao(): ActivityTransitionDao
}