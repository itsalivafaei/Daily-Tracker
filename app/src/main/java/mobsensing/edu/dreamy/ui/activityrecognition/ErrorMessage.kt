package mobsensing.edu.dreamy.ui.activityrecognition

import androidx.annotation.StringRes
import java.time.Instant

enum class Play

data class ErrorMessage(
    @StringRes
    val resId: Int,
    val time: Instant = Instant.now()
)