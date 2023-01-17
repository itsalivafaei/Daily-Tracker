package mobsensing.edu.dreamy.data.sleep

import kotlinx.coroutines.flow.Flow
import mobsensing.edu.dreamy.data.sleep.db.SleepClassifyEventEntity
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity

interface Repository {
    val subscription: Flow<Boolean>
    val classifyEvents: Flow<List<SleepClassifyEventEntity>>
    val segmentEvents: Flow<List<SleepSegmentEventEntity>>
}