package com.example.dailytracker.dataModule.sleep.datastore

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

/*
* Saves the sleep data subscription status into a [DataStore].
* Used to check if the app is still listening to changes in sleep data when the app is brought
* back into the foreground.
*/
class SleepSubscriptionStatus(private val dataStore: DataStore<Preferences>) {
    private companion object  {
        val SUBSCRIBED_TO_SLEEP_DATA = booleanPreferencesKey("subscribed_to_sleep_data")
        //Test
        const val TAG = "SleepSubscriptionPreferencesRepo"
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