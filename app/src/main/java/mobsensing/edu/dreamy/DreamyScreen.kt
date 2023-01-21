package mobsensing.edu.dreamy

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mobsensing.edu.dreamy.ui.OverviewScreen
import mobsensing.edu.dreamy.ui.activityrecognition.ActivityRecognitionViewModel
import mobsensing.edu.dreamy.ui.main.SleepScreen
import mobsensing.edu.dreamy.ui.main.SleepViewModel

const val TAG = "DreamyScreen"

// * Enum values that represent the screens in the app
enum class DreamyScreen(@StringRes val title: Int) {
    Overview(title = R.string.app_name),
    Sleep(title = R.string.sleep_report),
    ActivityRecognition(title = R.string.activity_report)
}

// * Composable that displays the topBar and displays back button if back navigation is possible.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamyAppBar(
    currentScreen: DreamyScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(id = currentScreen.title))},
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamyApp(
    modifier: Modifier = Modifier,
    sleepViewModel: SleepViewModel = viewModel(factory = SleepViewModel.Factory),
    activityRecognitionViewModel: ActivityRecognitionViewModel = viewModel(factory = ActivityRecognitionViewModel.Factory),
    // TODO: Impl this
//    permissionState: ActivityRecognitionPermissionState,
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = DreamyScreen.valueOf(
        backStackEntry?.destination?.route ?: DreamyScreen.Overview.name
    )

    // Showing snackbar when there's an error requesting updates.
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            DreamyAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        val sleepUiState by sleepViewModel.uiState.collectAsState()
        val sleepRepo by sleepViewModel.repositoryState.collectAsState()

        val lastActivityRecord by activityRecognitionViewModel.lastTransitionEvent.collectAsState()

        // ! Using LocalContext instead of applicationContext
        NavHost(
            navController = navController,
            startDestination = DreamyScreen.Overview.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = DreamyScreen.Overview.name) {
                val context = LocalContext.current
                val record = lastActivityRecord.ifEmpty {
                    Log.d(TAG, "the record is empty")
                    null
                }

                OverviewScreen(
                    context = context,
                    onActivityCardClick = { navController.navigate(DreamyScreen.ActivityRecognition.name) },
                    lastActivityStartingTimestamp = "lastActivityRecord.last().timestamp.toString()",
                    lastActivityType = "lastActivityRecord.last().activityType.name",
                    lastActivityImage = R.drawable.sitting_girl,
                    onSleepCardClick = { navController.navigate(DreamyScreen.Sleep.name) },
                    lastSleepDate = "lastSleepDate",
                    lastSleepDuration = "lastSleepDuration",
                    lastSleepQualityImage = R.drawable.sitting_girl
                )
            }
            composable(route = DreamyScreen.Sleep.name) {
                val context = LocalContext.current

                SleepScreen(
                    applicationContext = context,
                    sleepPermission = 0
                )
            }
            composable(route = DreamyScreen.ActivityRecognition.name) {

            }
        }
    }
}