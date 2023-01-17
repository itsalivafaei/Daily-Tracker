package mobsensing.edu.dreamy

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.ActivityRecognition
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import mobsensing.edu.dreamy.data.activityRecognition.db.ActivityRecognitionDatabase
//import com.example.dailytracker.data.sleep.SleepRepository
//import com.example.dailytracker.data.sleep.datastore.SleepSubscriptionStatus
//import com.example.dailytracker.data.sleep.db.SleepDatabase
import mobsensing.edu.dreamy.data.sleep.SleepRepository
import mobsensing.edu.dreamy.data.sleep.datastore.SleepSubscriptionStatus
import mobsensing.edu.dreamy.data.sleep.db.SleepDatabase
import javax.inject.Singleton

/**
 * Sets up repository for all sleep data.
 */
private const val SLEEP_SUBSCRIPTION_PREFERENCE_NAME = "sleep_subscription_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SLEEP_SUBSCRIPTION_PREFERENCE_NAME
)

/** Custom app entry point for manual dependency injection **/
@HiltAndroidApp
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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGoogleApiAvailability() = GoogleApiAvailability.getInstance()

    @Provides
    @Singleton
    fun provideDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            application.preferencesDataStoreFile("prefs")
        }
    }

    @Provides
    @Singleton
    fun provideActivityRecognitionClient(application: Application) =
        ActivityRecognition.getClient(application)

    @Provides
    @Singleton
    fun provideDatabase(application: Application): ActivityRecognitionDatabase {
        return Room.databaseBuilder(
            application,
            ActivityRecognitionDatabase::class.java,
            "activity_recognition_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideActivityTransitionDao(db: ActivityRecognitionDatabase) =
        db.getActivityTransitionRecordDao()
}