package mobsensing.edu.dreamy.data.sleep

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
    val subscribedToSleepDataFlow: Flow<Boolean> = sleepSubscriptionStatus.subscribedToSleepDataFlow

    suspend fun updateSubscribedToSleepData(subscribedToSleepData: Boolean) =
        sleepSubscriptionStatus.updateSubscribedToSleepData(subscribedToSleepData)

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