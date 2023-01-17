package mobsensing.edu.dreamy.ui.main

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mobsensing.edu.dreamy.ui.DisplaySnackbar
import mobsensing.edu.dreamy.ui.theme.DailyTrackerTheme

private const val TAG = "MainScreen"


@Composable
fun MainUi(
    modifier: Modifier = Modifier,
    applicationContext: Context,
    sleepPermission: Int,
    mainViewModel: NewViewModel = viewModel(factory = NewViewModel.Factory)
) {
    val sleepUiState by mainViewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Body(header = sleepUiState.header, body = sleepUiState.sleepData)
        Spacer(modifier = modifier.padding(top = 8.dp))
        SubscriptionBtn(text = sleepUiState.buttonText, context = applicationContext) {
            mainViewModel.onClickRequestSleepData(
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
        MainUi(applicationContext = LocalContext.current, sleepPermission = 0)
    }
}
