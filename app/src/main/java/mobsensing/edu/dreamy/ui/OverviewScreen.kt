package mobsensing.edu.dreamy.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobsensing.edu.dreamy.R
import mobsensing.edu.dreamy.ui.components.CardDisplay

@Composable
fun OverviewScreen(
    context: Context,
    onActivityCardClick: () -> Unit = {},
    lastActivityStartingTimestamp: String,
    lastActivityType: String,
    lastActivityImage:Int,
    onSleepCardClick: () -> Unit = {},
    lastSleepDate: String,
    lastSleepDuration: String,
    lastSleepQualityImage: Int,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CardDisplay(
                cardTitle = context.getString(R.string.overview_activity_title),
                cardSubtitle = lastActivityStartingTimestamp,
                durationOrType = lastActivityType,
                image = lastActivityImage,
                onCardClick = onActivityCardClick
            )
        }

        item {
            CardDisplay(
                cardTitle = context.getString(R.string.overview_sleep_title),
                cardSubtitle = lastSleepDate,
                durationOrType = lastSleepDuration,
                image = lastSleepQualityImage,
                onCardClick = onSleepCardClick
            )
        }

        item {
            Card(
                modifier = modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.overview_title),
                        modifier = modifier
                            .align(Alignment.Start),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = modifier.height(4.dp))
                    Text(
                        text = context.getString(R.string.overview_subtitle, "s"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = modifier.height(24.dp))

                    DeviceStatus(context = context)
                }
            }
        }
    }
}

@Composable
fun DeviceStatus(modifier: Modifier = Modifier, context: Context) {
    Column(
        modifier = modifier
            .fillMaxWidth()) {
        StatusRow(
            context = context,
            title = R.string.overview_play_services_status,
            status = "Status"
        )
    }
}

@Composable
fun StatusRow(
    modifier: Modifier = Modifier,
    context: Context,
    title: Int,
    status: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ){
        Text(
            text = context.getString(title),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            modifier = modifier
                .weight(1f)
        )
        Text(
            text = status,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = modifier
                .weight(1f)
        )
    }
}