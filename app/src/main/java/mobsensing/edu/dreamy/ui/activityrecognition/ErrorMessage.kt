package mobsensing.edu.dreamy.ui.activityrecognition

import androidx.annotation.StringRes
import java.time.Instant

data class ErrorMessage(
    @StringRes
    val resId: Int,
    val time: Instant = Instant.now()
)