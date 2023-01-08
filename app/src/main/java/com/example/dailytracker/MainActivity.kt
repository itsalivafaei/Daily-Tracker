package com.example.dailytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailytracker.uiModule.theme.DailyTrackerTheme
import com.example.dailytracker.utilityModule.GoogleServices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val status: Boolean
        val sleep: Boolean
        //Granting permissions
        GoogleServices(this).let {
            status = it.googlePlayServiceAvailabilityCheck()
            sleep = it.activityApproved()
        }

        setContent {
            DailyTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(status.toString(), sleep.toString())
                }
            }
        }
    }
}

@Composable
fun Greeting(status: String, sleep: String) {
        Text(
            text = "Play Service availability: $status. and sleep: $sleep",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.CenterVertically),
            fontSize = 24.sp
        )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DailyTrackerTheme {
        Greeting("Default", "Default")
    }
}