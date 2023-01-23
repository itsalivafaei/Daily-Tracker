package mobsensing.edu.dreamy

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.ActivityRecognition
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import mobsensing.edu.dreamy.data.activityRecognition.ActivityRecognitionRepository
import mobsensing.edu.dreamy.data.activityRecognition.ActivityTransitionManager
import mobsensing.edu.dreamy.data.activityRecognition.datastore.ActivityTransitionUpdateStatus
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityRecognitionDatabase
//import com.example.dailytracker.data.sleep.SleepRepository
//import com.example.dailytracker.data.sleep.datastore.SleepSubscriptionStatus
//import com.example.dailytracker.data.sleep.db.SleepDatabase
import mobsensing.edu.dreamy.data.sleep.SleepRepository
import mobsensing.edu.dreamy.data.sleep.datastore.SleepSubscriptionStatus
import mobsensing.edu.dreamy.data.sleep.db.SleepDatabase
import mobsensing.edu.dreamy.util.PlayServicesAvailabilityChecker
import javax.inject.Singleton

private const val SLEEP_SUBSCRIPTION_PREFERENCE_NAME = "sleep_subscription_preferences"
private val Context.sleepDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SLEEP_SUBSCRIPTION_PREFERENCE_NAME
)

private const val ACTIVITY_TRANSITION_UPDATE_PREFERENCE_NAME = "activity_transition_update_preferences"
private val Context.activityTransitionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = ACTIVITY_TRANSITION_UPDATE_PREFERENCE_NAME
)

/** Custom app entry point for manual dependency injection **/
@RequiresApi(Build.VERSION_CODES.S)
class MainApplication : Application() {
    private val sleepDatabase by lazy {
        SleepDatabase.getDatabase(applicationContext)
    }

    val sleepRepository by lazy {
        SleepRepository(
            sleepSubscriptionStatus = SleepSubscriptionStatus(sleepDataStore),
            sleepSegmentEventDao = sleepDatabase.sleepSegmentEventDao(),
            sleepClassifyEventDao = sleepDatabase.sleepClassifyEventDao()
        )
    }

    private val activityDatabase by lazy {
        ActivityRecognitionDatabase.getDatabase(applicationContext)
    }

    val activityRecognitionRepository by lazy {
        ActivityRecognitionRepository(
            activityTransitionUpdateStatus = ActivityTransitionUpdateStatus(activityTransitionDataStore),
            activityTransitionDao = activityDatabase.activityTransitionDao()
        )
    }

    val playServicesState by lazy {
        PlayServicesAvailabilityChecker(
            context = applicationContext,
            googleApiAvailability = GoogleApiAvailability.getInstance()
        )
    }

    val activityTransitionManager by lazy {
        ActivityTransitionManager(applicationContext)
    }
}