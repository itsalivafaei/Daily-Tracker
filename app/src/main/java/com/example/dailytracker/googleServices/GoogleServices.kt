package com.example.dailytracker.googleServices

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight

class GoogleServices(private val mainActivityInstance: Context) {
    private val googlePlayServicesAvailability: (Context, Int) -> Boolean = { mainInstance, minSdkVersion ->
        val googleApiAvailabilityLight: GoogleApiAvailabilityLight =
            GoogleApiAvailabilityLight.getInstance()
        val status = googleApiAvailabilityLight.isGooglePlayServicesAvailable(mainInstance, minSdkVersion)
        status == ConnectionResult.SUCCESS

    }


    fun googlePlayServiceAvailabilityCheck(): Boolean {
        return googlePlayServicesAvailability(mainActivityInstance, 29)
    }
}