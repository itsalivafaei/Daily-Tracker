package mobsensing.edu.dreamy.receiver.activityrecognition

/*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mobsensing.edu.dreamy.data.activityRecognition.ActivityRecognitionPreferences
import mobsensing.edu.dreamy.data.activityRecognition.ActivityTransitionManager
import mobsensing.edu.dreamy.data.activityRecognition.PlayServicesAvailabilityChecker
import javax.inject.Inject

*/
/**
 * Receiver that will restore the app's registration for activity transition updates if they were
 * interrupted, e.g. because the device rebooted.
 *//*

@AndroidEntryPoint
class ActivityBootReceiver : BroadcastReceiver() {
    @Inject lateinit var activityRecognitionPreferences: ActivityRecognitionPreferences
    @Inject lateinit var playServicesAvailabilityChecker: PlayServicesAvailabilityChecker
    @Inject lateinit var activityTransitionManager: ActivityTransitionManager
    private val scope = MainScope()
    companion object {
        val VALID_ACTIONS = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOK_POWERON"
        )
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in VALID_ACTIONS) return

        scope.launch {
            val turnOn = activityRecognitionPreferences.isActivityTransitionUpdatesTurnedOn.first()
            if (!turnOn) return@launch

            // The user previously turned on activity transition updates. Try to request updates.
            //val success = playServicesAvailabilityChecker
        }
    }
}*/
