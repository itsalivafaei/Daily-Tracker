package mobsensing.edu.dreamy.data.activityRecognition

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.datastore.preferences.core.Preferences

//class ActivityRecognitionPreferences @Inject constructor(
//    private val dataStore: DataStore<Preferences>
//) {
//    private companion object {
//        val ACTIVITY_TRANSITION_UPDATES_TURNED_ON =
//            booleanPreferencesKey("activity_transition_updates_on")
//    }
//
//    val isActivityTransitionUpdatesTurnedOn = dataStore.data.map {
//        it[ACTIVITY_TRANSITION_UPDATES_TURNED_ON] ?: false
//    }
//
//    suspend fun setActivityTransitionUpdatesTurnedOn(isOn: Boolean) {
//        dataStore.edit {
//            it[ACTIVITY_TRANSITION_UPDATES_TURNED_ON] = isOn
//        }
//    }
//}

class ActivityRecognitionPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
    ) {
    private companion object {
        val ACTIVITY_TRANSITION_UPDATES_TURNED_ON =
            booleanPreferencesKey("activity_transition_updates_on")
        const val TAG = "ActivityRecognitionPreferencesDatastore"
    }

    val isActivityTransitionUpdatesTurnedOn = dataStore.data.map { preferences ->
        preferences[ACTIVITY_TRANSITION_UPDATES_TURNED_ON] ?: false
    }

    suspend fun setActivityTransitionUpdatesTurnedOn(isOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[ACTIVITY_TRANSITION_UPDATES_TURNED_ON] = isOn
        }
    }
}