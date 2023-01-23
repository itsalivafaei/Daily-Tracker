package mobsensing.edu.dreamy.data.sleep.db

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE_NAME = "sleep_segments_database"
@Database(
    entities = [SleepSegmentEventEntity::class, SleepClassifyEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SleepDatabase : RoomDatabase() {

    abstract fun sleepSegmentEventDao(): SleepSegmentEventDao
    abstract fun sleepClassifyEventDao(): SleepClassifyEventDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        fun getDatabase(context: Context): SleepDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    SleepDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}