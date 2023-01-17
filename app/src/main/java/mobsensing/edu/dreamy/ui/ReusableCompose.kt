package mobsensing.edu.dreamy.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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