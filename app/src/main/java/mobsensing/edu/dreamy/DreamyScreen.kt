package mobsensing.edu.dreamy

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityTransitionRecord
import mobsensing.edu.dreamy.data.activityRecognition.db.DetectedActivityType
import mobsensing.edu.dreamy.data.activityRecognition.db.DetectedTransitionType
import mobsensing.edu.dreamy.data.sleep.db.SleepSegmentEventEntity
import mobsensing.edu.dreamy.ui.OnboardingScreen
import mobsensing.edu.dreamy.ui.OverviewScreen
import mobsensing.edu.dreamy.ui.OverviewViewModel
import mobsensing.edu.dreamy.ui.PlayServicesAvailableState
import mobsensing.edu.dreamy.ui.activityrecognition.ActivityRecognitionViewModel
import mobsensing.edu.dreamy.ui.activityrecognition.ActivityScreen
import mobsensing.edu.dreamy.ui.sleep.SleepScreen
import mobsensing.edu.dreamy.ui.sleep.SleepViewModel
import mobsensing.edu.dreamy.util.ActivityRecognitionPermissionState
import java.time.Instant

const val TAG = "DreamyScreen"

// * Enum values that represent the screens in the app
enum class DreamyScreen(@StringRes val title: Int) {
    Onboard(title = R.string.onboarding),
    Initializing(title = R.string.initializing),
    ServiceUnavailable(title = R.string.service_unavailable),
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
    CenterAlignedTopAppBar(
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

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamyApp(
    modifier: Modifier = Modifier,
    overviewViewModel: OverviewViewModel = viewModel(factory = OverviewViewModel.Factory),
    sleepViewModel: SleepViewModel = viewModel(factory = SleepViewModel.Factory),
    activityRecognitionViewModel: ActivityRecognitionViewModel = viewModel(factory = ActivityRecognitionViewModel.Factory),
    permissionState: ActivityRecognitionPermissionState,
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = DreamyScreen.valueOf(
        backStackEntry?.destination?.route ?: DreamyScreen.Overview.name
    )

    // * Showing snackbar when there's an error requesting updates.
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var whichScreen: String by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            if (whichScreen != DreamyScreen.Onboard.name) {
                DreamyAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = if (whichScreen != DreamyScreen.Overview.name) {
                        navController.previousBackStackEntry != null
                    } else {
                        false
                    },
                    navigateUp = { navController.navigateUp() }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        // * OverviewViewModel
        val playServicesState by overviewViewModel.playServicesAvailableState.collectAsState()
        // * ActivityRecognitionViewModel
        val mostRecentTransitionEvents by activityRecognitionViewModel.mostRecentTransitionEvents.collectAsState()
        val isOn by activityRecognitionViewModel.activityTransitionUpdateDataFlow.collectAsState()
        // * SleepViewModel
        val sleepRepo by sleepViewModel.repositoryState.collectAsState()

        Log.d("ISON", "activity:$isOn")
        Log.d("ISON", "sleep:${sleepRepo.subscribedToSleepData}")


        NavHost(
            navController = navController,
            startDestination = DreamyScreen.Onboard.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = DreamyScreen.Onboard.name) {
                whichScreen = DreamyScreen.Onboard.name
                Log.d("MTEST", "DreamyScreen, onboarding composable")

                OnboardingScreen(
                    onStartClick = {
                        when (playServicesState) {
                            PlayServicesAvailableState.Initializing -> {
                                navController.navigate(DreamyScreen.Initializing.name)
                            }
                            PlayServicesAvailableState.PlayServicesUnavailable -> {
                                navController.navigate(DreamyScreen.ServiceUnavailable.name)
                            }
                            PlayServicesAvailableState.PlayServicesAvailable -> {
                                navController.navigate(DreamyScreen.Overview.name)
                            }
                        }
                    }
                )
            }
            composable(route = DreamyScreen.Overview.name) {
                whichScreen = DreamyScreen.Overview.name
                val context = LocalContext.current
                val record = mostRecentTransitionEvents.ifEmpty {
                    Log.d(TAG, "the record is empty")
                    null
                }

                OverviewScreen(
                    context = context,

                    // Switch buttons
                    toggleActivityTransitionUpdates = {
                        // ? Replaced
                        // activityRecognitionViewModel.toggleActivityTransitionUpdates()
                        activityRecognitionViewModel.toggle(context)
                    },
                    isActivityUpdatesTurnedOn = isOn,

                    toggleRequestSleepData = { sleepViewModel.toggleRequestSleepData(context) },
                    isSubscribedToSleepData = sleepRepo.subscribedToSleepData,

                    // Permissions
                    onClickRequest = { permissionState.requestPermission() },
                    isGranted = permissionState.permissionGranted,
                    showDegradedExperience = permissionState.showDegradedExperience,
                    needsPermissionRationale = permissionState.needsRationale,

                    // Activity Recognition
                    onActivityCardClick = { navController.navigate(DreamyScreen.ActivityRecognition.name) },
                    transitionEvents = mostRecentTransitionEvents,
                    lastActivityImage = R.drawable.sitting_girl,

                    // Sleep Data
                    onSleepCardClick = { navController.navigate(DreamyScreen.Sleep.name) },
                    sleepEvents = sleepRepo.sleepSegmentEvents,
                    lastSleepQualityImage = R.drawable.sitting_girl
                )
            }


            composable(route = DreamyScreen.Sleep.name) {
                whichScreen = DreamyScreen.Sleep.name
                val context = LocalContext.current

                SleepScreen(
                    context = context,
                    sleepEvents = sleepRepo.sleepSegmentEvents
                )
            }


            composable(route = DreamyScreen.ActivityRecognition.name) {
                whichScreen = DreamyScreen.ActivityRecognition.name
                val context = LocalContext.current

                ActivityScreen(
                    context = context,
                    transitionEvents = mostRecentTransitionEvents
                    // ? Test
//                    transitionEvents = transitionTestEvent
                )
            }
        }
    }
}