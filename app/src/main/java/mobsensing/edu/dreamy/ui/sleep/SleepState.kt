package mobsensing.edu.dreamy.ui.sleep

import mobsensing.edu.dreamy.data.sleep.db.SleepClassifyEventEntity
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity

data class SleepState(
    val subscribedToSleepData: Boolean = false,
    val sleepSegmentEvents: List<SleepSegmentEventEntity> =
        listOf<SleepSegmentEventEntity>(),
    val sleepClassifyEvents: List<SleepClassifyEventEntity> =
        listOf<SleepClassifyEventEntity>()
)

data class RepositoryState(
    var subscribedToSleepData: Boolean = false,
    var sleepSegmentEvents: List<SleepSegmentEventEntity> =
        listOf<SleepSegmentEventEntity>(),
    var sleepClassifyEvents: List<SleepClassifyEventEntity> =
        listOf<SleepClassifyEventEntity>()
)


data class Output(
    var header: String = "header init",
    var sleepData: String = "data init",
//    var screenOutput: String = "header and data init",
    var buttonText: String = "Subscribe"
)
