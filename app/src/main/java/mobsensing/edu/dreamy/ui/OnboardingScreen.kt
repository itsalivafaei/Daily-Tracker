package mobsensing.edu.dreamy.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingScreen(onStartClick: () -> Unit = {}) {
    Column {
        Button(
            onClick = onStartClick
        ) {
            Text(text = "Click me")
        }
    }
}