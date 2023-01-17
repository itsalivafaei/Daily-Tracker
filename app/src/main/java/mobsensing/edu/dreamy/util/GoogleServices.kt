package mobsensing.edu.dreamy.util

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GoogleApiAvailabilityLight
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleServices(private val mainActivityContext: Context) {
    private val googlePlayServicesAvailability: (Context, Int) -> Boolean = { mainInstance, minSdkVersion ->
        val googleApiAvailabilityLight: GoogleApiAvailabilityLight =
            GoogleApiAvailabilityLight.getInstance()
        val status = googleApiAvailabilityLight.isGooglePlayServicesAvailable(mainInstance, minSdkVersion)
        status == ConnectionResult.SUCCESS
    }

    fun googlePlayServiceAvailabilityCheck(): Boolean {
        //activity recognition works on minimum SDK 29
        return googlePlayServicesAvailability(mainActivityContext, 29)
    }
}

/**
 * From Activity Recognition -> Utility class which checks for the availability of Google Play Services on this device.
 */
