package mobsensing.edu.dreamy.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobsensing.edu.dreamy.R
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity
import mobsensing.edu.dreamy.ui.components.CardDisplay
import mobsensing.edu.dreamy.ui.components.SwitchDisplay
import mobsensing.edu.dreamy.util.durationConverter
import mobsensing.edu.dreamy.util.epochMilliToDayMonth

// * Main view
@Composable
fun OverviewScreen(
    context: Context,
    toggleActivityTransitionUpdates: () -> Unit,
    toggleRequestSleepData: () -> Unit,
    onClickRequest: () -> Unit,
    isGranted: Boolean,
    isActivityUpdatesTurnedOn: Boolean,
    isSubscribedToSleepData: Boolean,
    showDegradedExperience: Boolean,
    needsPermissionRationale: Boolean,
    onActivityCardClick: () -> Unit,
    lastActivityStartingTimestamp: String,
    lastActivityType: String,
    lastActivityImage:Int,
    onSleepCardClick: () -> Unit,
    lastSleepEvent: SleepSegmentEventEntity,
    lastSleepQualityImage: Int,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    var screenDescription by remember { mutableStateOf(R.string.request_permission) }
    var showRationaleDialog by remember { mutableStateOf(false) }
    if (showRationaleDialog) {
        PermissionRationaleDialog(
            onConfirm = {
                showRationaleDialog = false
                onClickRequest()
            },
            onDismiss = {
                showRationaleDialog = false
            }
        )
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
    ) {
        // ? Header
        // * App name
/*
        Text(
            text = "Dreamy",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            modifier = modifier
                .fillMaxWidth()
        )
*/
        Text(
            text = stringResource(id = screenDescription),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            modifier = modifier
                .fillMaxWidth()
        )

        if (!isGranted){
            if (showDegradedExperience and needsPermissionRationale) {
                screenDescription = R.string.please_allow_permission
            } else if (showDegradedExperience and !needsPermissionRationale) {
                screenDescription = R.string.permission_denied
            }

            Button(
                onClick = {
                    if (needsPermissionRationale) {
                        showRationaleDialog = true
                    } else {
                        onClickRequest()
                    }
                },
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.request_permission_btn))
            }

        } else {
            screenDescription = R.string.permission_granted

            // ? Device status
            DeviceStatus(context = context)
            ToggleAction(
                title = R.string.overview_activity_title,
                isToggleOn = isActivityUpdatesTurnedOn
            ) {
                toggleActivityTransitionUpdates()
            }

            Divider(thickness = 1.dp)

            // ! Test
            Text(text = "Activity updates: $isActivityUpdatesTurnedOn")

            ToggleAction(
                title = R.string.overview_sleep_subscription_status,
                isToggleOn = isSubscribedToSleepData
            ) {
                toggleRequestSleepData()
            }
        }

        // ? Body
        Spacer(modifier = modifier.height(24.dp))

        CardDisplay(
            cardTitle = context.getString(R.string.overview_activity_title),
            cardSubtitle = lastActivityStartingTimestamp,
            durationOrType = lastActivityType,
            image = lastActivityImage,
            onCardClick = onActivityCardClick
        )

        CardDisplay(
            cardTitle = context.getString(R.string.overview_sleep_title),
            cardSubtitle = epochMilliToDayMonth(
                lastSleepEvent.endTimeMillis
            ),
            durationOrType = durationConverter(
                start = lastSleepEvent.startTimeMillis,
                end = lastSleepEvent.endTimeMillis
            ),
            image = lastSleepQualityImage,
            onCardClick = onSleepCardClick
        )
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
                .weight(1.5f)
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


@Composable
fun ToggleAction(title: Int, modifier: Modifier = Modifier, isToggleOn: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = title),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
            modifier = modifier
                .weight(1.5f)
        )
        SwitchDisplay(isToggleOn = isToggleOn, onToggle = onToggle)
    }
}


// * Permission check (from location)
@Composable
fun PermissionRationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.permission_rationale_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.permission_rationale_dialog_message))
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    )
}




