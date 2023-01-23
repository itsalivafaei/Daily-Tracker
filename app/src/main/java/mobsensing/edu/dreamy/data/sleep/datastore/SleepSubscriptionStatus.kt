package mobsensing.edu.dreamy.data.sleep.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val SLEEP_PREFERENCES_NAME = "sleep_preferences"

class SleepSubscriptionStatus(private val dataStore: DataStore<Preferences>) {
    private companion object  {
        val SUBSCRIBED_TO_SLEEP_DATA = booleanPreferencesKey("subscribed_to_sleep_data")
        const val TAG = "SleepSubscriptionPreferencesDatastore"
    }

    // Observed Flow will notify the observer when the the sleep subscription status has changed.
    val subscribedToSleepDataFlow: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
        // Get the subscription value, defaults to false if not set:
        preferences[SUBSCRIBED_TO_SLEEP_DATA] ?: false
    }

    // Updates subscription status.
    suspend fun updateSubscribedToSleepData(subscribedToSleepData: Boolean) {
        dataStore.edit { preferences ->
            preferences[SUBSCRIBED_TO_SLEEP_DATA] = subscribedToSleepData
        }
    }
}