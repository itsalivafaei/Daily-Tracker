package mobsensing.edu.dreamy

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
//import com.example.dailytracker.data.sleep.SleepRepository
//import com.example.dailytracker.data.sleep.datastore.SleepSubscriptionStatus
//import com.example.dailytracker.data.sleep.db.SleepDatabase
import mobsensing.edu.dreamy.data.sleep.SleepRepository
import mobsensing.edu.dreamy.data.sleep.datastore.SleepSubscriptionStatus
import mobsensing.edu.dreamy.data.sleep.db.SleepDatabase

/**
 * Sets up repository for all sleep data.
 */
private const val SLEEP_SUBSCRIPTION_PREFERENCE_NAME = "sleep_subscription_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SLEEP_SUBSCRIPTION_PREFERENCE_NAME
)

/** Custom app entry point for manual dependency injection **/
class MainApplication : Application() {
    // Both database and repository use lazy so they aren't created when the app starts, but only
    // when repository is first needed.
    private val database by lazy {
        SleepDatabase.getDatabase(applicationContext)
    }

    val sleepRepository by lazy {
        SleepRepository(
            sleepSubscriptionStatus = SleepSubscriptionStatus(dataStore),
            sleepSegmentEventDao = database.sleepSegmentEventDao(),
            sleepClassifyEventDao = database.sleepClassifyEventDao()
        )
    }
}