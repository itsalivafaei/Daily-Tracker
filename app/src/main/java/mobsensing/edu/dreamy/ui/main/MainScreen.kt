package mobsensing.edu.dreamy.ui.main

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

//val mainViewModel: MainViewModel = MainViewModel()
private const val TAG = "MainScreen"
//private val permission = permissionsMap["ACTIVITY_RECOGNITION"]


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    applicationContext: Context,
    sleepPermission: Int,
    mainViewModel: MainViewModel =
        viewModel( factory = MainViewModel.Factory )
) {
    // ? As UI State
    // ? check view model
    //val sleepState by mainViewModel.mainSleepState.collectAsState()
    var shownResult by rememberSaveable { mutableStateOf(" ")}

/*
    if () {
        shownResult += mainViewModel.updateOutput()
        LogSleepState(shownResult)
    }
*/

/*
    if (isPermissionGranted(applicationContext, permission!!)) {
        SubscriptionButton(mainViewModel.subscriptionButtonText) {
            mainViewModel.onClickRequestSleepData(applicationContext)
        }
    } else {
        // TODO: Implement permission request from compose
        SubscriptionButton(text = "Approve permission")
    }
*/
}

// ? Need for showing Rational permission request to user
@Composable
fun PermissionSettingSnackBar(){

}

@Composable
fun LogSleepState(text: String) {

}

@Composable
fun SubscriptionButton(text: String, onClickListener: (Context) -> Unit = {}) {

}
