package mobsensing.edu.dreamy.data.sleep.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Defines [SleepSegmentEventEntity] database operations.
 */
@Dao
interface SleepSegmentEventDao {
    @Query("SELECT * from sleep_segment_events_table ORDER BY start_time_millis DESC")
    fun getAll(): Flow<List<SleepSegmentEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sleepSegmentEventEntity: SleepSegmentEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sleepSegmentEventEntity: List<SleepSegmentEventEntity>)

    @Delete
    suspend fun delete(sleepSegmentEventEntity: SleepSegmentEventEntity)

    @Query("DELETE FROM sleep_segment_events_table")
    suspend fun deleteAll()
}