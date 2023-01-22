package mobsensing.edu.dreamy.data.activityRecognition.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing [ActivityTransitionRecord]s in the database.
 */
@Dao
interface ActivityTransitionDao {

    @Query("SELECT * FROM activity_transition_records_table ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ActivityTransitionRecord>>

    @Query("SELECT * FROM activity_transition_records_table ORDER BY timestamp DESC LIMIT 20")
    fun getMostRecent(): Flow<List<ActivityTransitionRecord>>

    @Insert
    suspend fun insert(record: ActivityTransitionRecord)

    @Insert
    suspend fun insertAll(records: List<ActivityTransitionRecord>)

    @Delete
    suspend fun delete(record: ActivityTransitionRecord)

    @Query("DELETE FROM activity_transition_records_table")
    suspend fun deleteAll()
}