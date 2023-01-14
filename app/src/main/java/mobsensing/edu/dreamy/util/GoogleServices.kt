package mobsensing.edu.dreamy.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight

class GoogleServices(private val mainActivityContext: Context) {
    private val googlePlayServicesAvailability: (Context, Int) -> Boolean = { mainInstance, minSdkVersion ->
        val googleApiAvailabilityLight: GoogleApiAvailabilityLight =
            GoogleApiAvailabilityLight.getInstance()
        val status = googleApiAvailabilityLight.isGooglePlayServicesAvailable(mainInstance, minSdkVersion)
        status == ConnectionResult.SUCCESS
    }

    //Sleep Permission check
    private val activityRecognitionPermissionApproved: () -> Boolean = {
        PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            mainActivityContext,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }


    fun googlePlayServiceAvailabilityCheck(): Boolean {
        //activity recognition works on minimum SDK 29
        return googlePlayServicesAvailability(mainActivityContext, 29)
    }

    fun activityApproved(): Boolean {
        return activityRecognitionPermissionApproved()
    }

    fun onClickRequestSleepData(view: View) {
        if (activityRecognitionPermissionApproved()) {
        }
    }
}