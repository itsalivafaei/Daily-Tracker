package mobsensing.edu.dreamy.ui.main

//import com.example.dailytracker.data.sleep.db.SleepClassifyEventEntity
//import com.example.dailytracker.data.sleep.db.SleepSegmentEventEntity
import android.util.Log
import kotlinx.coroutines.flow.Flow
import mobsensing.edu.dreamy.data.sleep.SleepRepository
import mobsensing.edu.dreamy.data.sleep.db.SleepClassifyEventEntity
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity

data class SleepState(
    val subscribedToSleepData: Boolean = false,
    val sleepSegmentEvents: List<SleepSegmentEventEntity> =
        listOf<SleepSegmentEventEntity>(),
    val sleepClassifyEvents: List<SleepClassifyEventEntity> =
        listOf<SleepClassifyEventEntity>()
)


/*
data class RepositoryState(private val repository: SleepRepository) {
    val subscribedToSleepData: Flow<Boolean> = repository.subscribedToSleepDataFlow

    val sleepSegmentEvents: Flow<List<SleepSegmentEventEntity>> = repository.segmentEvents

    val sleepClassifyEvents: Flow<List<SleepClassifyEventEntity>> = repository.classifyEvents
}
*/
data class RepositoryState(
    var subscribedToSleepData: Boolean = false,
    var sleepSegmentEvents: List<SleepSegmentEventEntity> =
        listOf<SleepSegmentEventEntity>(),
    var sleepClassifyEvents: List<SleepClassifyEventEntity> =
        listOf<SleepClassifyEventEntity>()
)


data class ReportState(
    var subscription: Boolean = false,
    var segmentEventOutput: String = " ",
    var classifyEventOutput: String = " "
)

data class Output(
    var header: String = "header init",
    var sleepData: String = "data init",
//    var screenOutput: String = "header and data init",
    var buttonText: String = "Subscribe"
)
