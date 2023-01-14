package mobsensing.edu.dreamy.data.sleep

/*
import com.example.dailytracker.data.sleep.datastore.SleepSubscriptionStatus
import com.example.dailytracker.data.sleep.db.SleepClassifyEventDao
import com.example.dailytracker.data.sleep.db.SleepClassifyEventEntity
import com.example.dailytracker.data.sleep.db.SleepSegmentEventDao
import com.example.dailytracker.data.sleep.db.SleepSegmentEventEntity
*/
import kotlinx.coroutines.flow.Flow
import mobsensing.edu.dreamy.data.sleep.datastore.SleepSubscriptionStatus
import mobsensing.edu.dreamy.data.sleep.db.SleepClassifyEventDao
import mobsensing.edu.dreamy.data.sleep.db.SleepClassifyEventEntity
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventDao
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity

class SleepRepository(
    private val sleepSubscriptionStatus: SleepSubscriptionStatus,
    private val sleepSegmentEventDao: SleepSegmentEventDao,
    private val sleepClassifyEventDao: SleepClassifyEventDao
) {
    /** Methods for SleepSubscriptionStatus **/
    // Uses [DataStore] to save the subscription to sleep data status. This is used to check if the
    // app is still listening to changes in sleep data when the app is brought back into
    // the foreground.
    val subscribedToSleepDataFlow: Flow<Boolean> = sleepSubscriptionStatus.subscribedToSleepDataFlow

    suspend fun updateSubscribedToSleepData(subscribedToSleepData: Boolean) =
        sleepSubscriptionStatus.updateSubscribedToSleepData(subscribedToSleepData)

    /** Methods for SleepSegmentEventDao **/
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    // By default Room runs suspend queries off the main thread. Therefore, we don't need to
    // implement anything else to ensure we're not doing long-running database work off the
    // main thread.
    val allSleepSegmentEvents: Flow<List<SleepSegmentEventEntity>> =
        sleepSegmentEventDao.getAll()

    suspend fun insertSleepSegment(sleepSegmentEventEntity: SleepSegmentEventEntity) =
        sleepSegmentEventDao.insert(sleepSegmentEventEntity)

    suspend fun insertSleepSegments(sleepSegmentEventEntities: List<SleepSegmentEventEntity>) =
        sleepSegmentEventDao.insertAll(sleepSegmentEventEntities)

    suspend fun deleteSleepSegment(sleepSegmentEventEntity: SleepSegmentEventEntity) =
        sleepSegmentEventDao.delete(sleepSegmentEventEntity)

    suspend fun deleteSleepSegments() =
        sleepSegmentEventDao.deleteAll()

    /** Methods for SleepClassifyEventDao **/
    // Observed Flow will notify the observer when the data has changed.
    val allSleepClassifyEvents: Flow<List<SleepClassifyEventEntity>> =
        sleepClassifyEventDao.getAll()

    suspend fun insertSleepClassifyEvent(sleepClassifyEventEntity: SleepClassifyEventEntity) =
        sleepClassifyEventDao.insert(sleepClassifyEventEntity)

    suspend fun insertSleepClassifyEvents(sleepClassifyEventEntity: List<SleepClassifyEventEntity>) =
        sleepClassifyEventDao.insertAll(sleepClassifyEventEntity)

    suspend fun deleteSleepClassifyEvent(sleepClassifyEventEntity: SleepClassifyEventEntity) =
        sleepClassifyEventDao.delete(sleepClassifyEventEntity)

    suspend fun deleteSleepClassifyEvents() =
        sleepClassifyEventDao.deleteAll()
}