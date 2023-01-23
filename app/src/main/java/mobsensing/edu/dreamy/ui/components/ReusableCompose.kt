package mobsensing.edu.dreamy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplaySnackbar(modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState()}
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            var clickCount  by remember { mutableStateOf(0) }
            ExtendedFloatingActionButton(
                onClick = {
                    // show snackbar as a suspend function
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Snackbar # ${++clickCount}",
                            actionLabel = "Action",
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
            ) {
                Text(text = "Show snackbar")
            }
        },
        content = { innerPadding ->
            Text(
                text = "Body content",
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }
    )
}

@Composable
fun CardDisplay(modifier: Modifier = Modifier, cardTitle: String, cardSubtitle: String, durationOrType: String, image: Int, onCardClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onCardClick
            )
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
        ) {
            Text(
                text = cardTitle,
                modifier = modifier
                    .align(Alignment.Start),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = modifier.height(4.dp))
            Text(
                text = cardSubtitle,
                modifier = modifier
                    .align(Alignment.Start),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = modifier.height(24.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = durationOrType,
                    modifier = modifier
                        .weight(2f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Image(
                    painter = painterResource(id = image),
                    contentDescription = stringResource(id = image),
                    modifier = modifier
                        .weight(1.25f)
                )
            }
        }
    }
}

@Composable
fun SwitchDisplay(isToggleOn: Boolean, onToggle: () -> Unit) {
    var checked by remember { mutableStateOf(isToggleOn) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
            onToggle()
        },
        modifier = Modifier
            .semantics { contentDescription = "switch with icon" }
    )
}

