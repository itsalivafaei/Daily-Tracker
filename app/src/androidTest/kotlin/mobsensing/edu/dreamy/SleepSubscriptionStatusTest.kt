package mobsensing.edu.dreamy

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.*
import mobsensing.edu.dreamy.data.sleep.SleepRepository
import mobsensing.edu.dreamy.data.sleep.datastore.SleepSubscriptionStatus
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

// ? Test Preference dataStore
// ! Test not happened
/**
 * Codelab Github: https://github.com/googlecodelabs/android-datastore/tree/preferences_datastore
 * Youtube: https://www.youtube.com/watch?v=ZqlZnSdSqI4
 **/
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SleepSubscriptionStatusTest {
    private val testCoroutineDispatcher = StandardTestDispatcher()
    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())
//    private val testCoroutineScope =
//        createTestCoroutineScope(TestCoroutineDispatcher() + TestCoroutineExceptionHandler() + (testCoroutineDispatcher + Job()))
    private val testContext: Context = ApplicationProvider.getApplicationContext()

    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = testCoroutineScope,
            produceFile = { testContext.preferencesDataStoreFile("TEST_DATABASE_NAME") }
        )
    private val repository: SleepSubscriptionStatus = SleepSubscriptionStatus(testDataStore)

    @Test
    fun test_updateSubscribedToSleepDataFunction() = runTest {
//        assertEquals(repository.subscribedToSleepDataFlow(), )
    }
}