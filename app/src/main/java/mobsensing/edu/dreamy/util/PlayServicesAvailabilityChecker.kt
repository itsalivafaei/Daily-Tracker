package mobsensing.edu.dreamy.util

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayServicesAvailabilityChecker(
    private val context: Context,
    private val googleApiAvailability: GoogleApiAvailability
) {
    suspend fun isGoogleServicesAvailable(): Boolean =
        withContext(Dispatchers.Default) {
            when (googleApiAvailability.isGooglePlayServicesAvailable(context)) {
                ConnectionResult.SUCCESS -> true
                else -> false
            }
        }
}