package mobsensing.edu.dreamy.ui.sleep

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobsensing.edu.dreamy.R
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity
import mobsensing.edu.dreamy.util.durationConverter
import mobsensing.edu.dreamy.util.epochMilliToDayMonthHourMinute

private const val TAG = "MainScreen"

// ? Old - Single - Version
/*@Composable
fun SleepScreen(
    modifier: Modifier = Modifier,
    applicationContext: Context,
    sleepPermission: Int,
    mainViewModel: SleepViewModel = viewModel(factory = SleepViewModel.Factory)
) {
    val sleepUiState by mainViewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Body(header = sleepUiState.header, body = sleepUiState.sleepData)
        Spacer(modifier = modifier.padding(top = 8.dp))
        SubscriptionBtn(text = sleepUiState.buttonText, context = applicationContext) {
            mainViewModel.toggleRequestSleepData(
                applicationContext
            )
        }
        Spacer(modifier = modifier.padding(top = 8.dp))
        DisplaySnackbar()
    }
}

@Composable
fun Body(header: String, body: String, modifier: Modifier = Modifier) {
    Column() {
        Text(
            text = header,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 17.sp,
            fontWeight = FontWeight.W700
        )
        Spacer(modifier = modifier.padding(top = 4.dp))
        Text(
            text = body,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
    }
}

@Composable
fun SubscriptionBtn(modifier: Modifier = Modifier, text: String, context: Context, onClickListener: (Context) -> Unit = {}) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    }
    Button(onClick = { onClickListener(context) },
    modifier = Modifier
        .fillMaxWidth(),
    ) {
        Text(text = text)
    }
}

@Composable
fun ExtraButton(){
}

@Preview
@Composable
fun SleepPreview() {
    DailyTrackerTheme {
        SleepScreen(applicationContext = LocalContext.current, sleepPermission = 0)
    }
}*/

// ? New Version
@Composable
fun SleepScreen(
    modifier: Modifier = Modifier,
    context: Context,
    sleepEvents: List<SleepSegmentEventEntity>
) {
    Log.d("SLEEP_EVENT","sleepEvents:$sleepEvents")

    if (sleepEvents.isEmpty()) {
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
        SleepEventsList(sleepEvents = sleepEvents)
    }
}

@Composable
fun SleepEventsList(modifier: Modifier = Modifier, sleepEvents: List<SleepSegmentEventEntity>) {
    Log.d("SLEEP_EVENT","fun sleepEvents:$sleepEvents")

    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        items(sleepEvents) {
            val status: String = when (it.status) {
                0 -> "Successful"
                1 -> "Missing Data"
                2 -> "Not Detected"
                else -> ""
            }

            if (it.status != 2) {
                val start = epochMilliToDayMonthHourMinute(it.startTimeMillis)
                val end = epochMilliToDayMonthHourMinute(it.endTimeMillis)
                val duration = durationConverter(it.startTimeMillis, it.endTimeMillis)

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                ) {
                    Column {
                        Text(text = status)
                        Text(text = "Start: $start, end: $end")
                    }
                    Text(text = duration)
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                ) {
                    Text(text = status)
                }
            }
            Divider(thickness = 1.dp)
        }
    }
}

// ? Return current time
//    fun timeConverter(time: Long): String {
//        val formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm")
//        val instant = Instant.ofEpochMilli(time)
//        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
//
//        return formatter.format(date)
//    }