package mobsensing.edu.dreamy.ui.activityrecognition

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobsensing.edu.dreamy.R
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityTransitionRecord
import mobsensing.edu.dreamy.util.durationConverter
import java.time.Instant

const val TAG = "ACTIVITY_TRANSITION_EVENT"

@Composable
fun ActivityScreen(
    modifier: Modifier = Modifier,
    context: Context,
    transitionEvents: List<ActivityTransitionRecord>
) {
    Log.d(TAG,"transitionEvents:$transitionEvents")

    if (transitionEvents.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.waiting_for_sleep_events),
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp
            )
        }
    } else {
        TransitionEventsList(transitionEvents = transitionEvents)
    }
}

@Composable
fun TransitionEventsList(
    modifier: Modifier = Modifier,
    transitionEvents: List<ActivityTransitionRecord>
) {
    Log.d(TAG,"TransitionEventsList():$transitionEvents")
    var currentRecordIndex = 0
    Log.d(TAG,"record index:$currentRecordIndex")


    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        items(transitionEvents.asReversed()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = it.activityType.name)
                if (it != transitionEvents.last()) {
                    Text(
                        text = durationConverter(
                            start = it.timestamp.toEpochMilli(),
                            end = transitionEvents.get(currentRecordIndex + 1).timestamp.toEpochMilli()
                        )
                    )
                    currentRecordIndex.inc()
                }  else {
                    Text(text = "Active")
                }
            }
        }
    }
}