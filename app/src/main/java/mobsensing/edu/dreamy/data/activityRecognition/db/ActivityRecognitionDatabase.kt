package mobsensing.edu.dreamy.data.activityRecognition.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ActivityTransitionRecord::class],
    version = 1,
    exportSchema = false
)
abstract class ActivityRecognitionDatabase : RoomDatabase() {
    abstract fun activityTransitionDao(): ActivityTransitionDao

    companion object {
        @Volatile
        private var INSTANCE: ActivityRecognitionDatabase? = null

        fun getDatabase(context: Context): ActivityRecognitionDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ActivityRecognitionDatabase::class.java,
                    "activity_recognition_database"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}