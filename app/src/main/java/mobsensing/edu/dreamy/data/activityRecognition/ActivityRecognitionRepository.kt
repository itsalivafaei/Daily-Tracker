package mobsensing.edu.dreamy.data.activityRecognition

import kotlinx.coroutines.flow.Flow
import mobsensing.edu.dreamy.data.activityRecognition.datastore.ActivityTransitionUpdateStatus
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityTransitionDao
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityTransitionRecord

class ActivityRecognitionRepository(
    private val activityTransitionUpdateStatus: ActivityTransitionUpdateStatus,
    private val activityTransitionDao: ActivityTransitionDao
) {
    // ? Impl ActivityTransitionUpdateStatus
    val activityTransitionUpdateDataFlow: Flow<Boolean> =
        activityTransitionUpdateStatus.activityTransitionUpdateDataFlow

    suspend fun updateActivityTransition(updateStatus: Boolean) =
        activityTransitionUpdateStatus.updateActivityTransitionData(updateStatus)

    // ? Impl ActivityTransitionDao
    val getAllTransitions: Flow<List<ActivityTransitionRecord>> =
        activityTransitionDao.getAll()

    val getMostRecentTransitions: Flow<List<ActivityTransitionRecord>> =
        activityTransitionDao.getMostRecent()

    suspend fun insertTransitionRecord(record: ActivityTransitionRecord) =
        activityTransitionDao.insert(record)

    suspend fun insertAllTransitionRecords(records: List<ActivityTransitionRecord>) =
        activityTransitionDao.insertAll(records)

    suspend fun deleteTransitionRecord(record: ActivityTransitionRecord) =
        activityTransitionDao.delete(record)

    suspend fun deleteAllTransitionRecords() =
        activityTransitionDao.deleteAll()
}