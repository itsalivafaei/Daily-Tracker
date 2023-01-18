package mobsensing.edu.dreamy

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mobsensing.edu.dreamy.ui.OverviewScreen

// * Enum values that represent the screens in the app
enum class DreamyScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
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
    viewModel: ViewModel,
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = DreamyScreen.valueOf(
        backStackEntry?.destination?.route ?: DreamyScreen.Start.name
    )

    Scaffold(
        topBar = {
            DreamyAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        // val uiState

        NavHost(
            navController = navController,
            startDestination = DreamyScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = DreamyScreen.Start.name) {
                OverviewScreen()
            }
            composable(route = DreamyScreen.Sleep.name) {

            }
            composable(route = DreamyScreen.ActivityRecognition.name) {

            }
        }
    }
}