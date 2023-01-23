package mobsensing.edu.dreamy.data.activityRecognition.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

class ActivityTransitionUpdateStatus(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val ACTIVITY_TRANSITION_UPDATES_TURNED_ON =
            booleanPreferencesKey("activity_transition_updates_on")
        const val TAG = "ActivityRecognitionPreferencesDatastore"
    }

    val activityTransitionUpdateDataFlow: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[ACTIVITY_TRANSITION_UPDATES_TURNED_ON] ?: false
        }

    suspend fun updateActivityTransitionData(updateStatus: Boolean) {
        dataStore.edit { preferences ->
            preferences[ACTIVITY_TRANSITION_UPDATES_TURNED_ON] = updateStatus
        }
    }
}