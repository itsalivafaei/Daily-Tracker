package com.example.dailytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dailytracker.uiModule.theme.DailyTrackerTheme
import com.example.dailytracker.utilityModule.GoogleServices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val status: Boolean
        //Granting permissions
        GoogleServices(this).let {
            status = it.googlePlayServiceAvailabilityCheck()
        }

        setContent {
            DailyTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(status.toString())
                }
            }
        }
    }
}

@Composable
fun Greeting(status: String) {
        Text(text = status)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DailyTrackerTheme {
        Greeting("Default")
    }
}