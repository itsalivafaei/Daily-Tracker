package mobsensing.edu.dreamy.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


val permissionsList = listOf(
    android.Manifest.permission.ACTIVITY_RECOGNITION
)

/**
 ? init here permissionsList as keys
 ? just change values
*/
private val permissionsMap = mutableMapOf(
    permissionsList[0] to PermissionStatus.INIT
)


/*
val permissionsMap: Map<String, String> = mapOf(
    "activity_recognition" to Manifest.permission.ACTIVITY_RECOGNITION
)

enum class Permissions(val value: String) {
    ACTIVITY_RECOGNITION(android.Manifest.permission.ACTIVITY_RECOGNITION)
}
*/

//Different states
/*
enum class PermissionStatus(val status: String) {
    INIT("init"),
    GRANTED("granted"),
    RATIONAL("rational"),
    REQUEST("request"),
    DENIED("denied")
}
*/
enum class PermissionStatus(val code: Int) {
    INIT(0),
    GRANTED(1),
    RATIONAL(2),
    REQUEST(3),
    DENIED(4)
}

/** Doc: https://developer.android.com/training/permissions/requesting**/
/*
var permissionStatus: PermissionStatus = PermissionStatus.INIT
*/

fun finalPermissionRequest(context: Context, activity: Activity, permissionCode: Int) : PermissionStatus {
    return when {
        !isPermissionGranted(context, permissionCode) -> {
            permissionsMap.set(permissionsList[permissionCode], PermissionStatus.REQUEST)
            permissionsMap.get(permissionsList[permissionCode])!!
        }
        isPermissionGranted(context, permissionCode) -> {
            permissionsMap.set(permissionsList[permissionCode], PermissionStatus.GRANTED)
            permissionsMap.get(permissionsList[permissionCode])!!
        }
        shouldRequestRational(activity, permissionCode) -> {
            permissionsMap.set(permissionsList[permissionCode], PermissionStatus.RATIONAL)
            permissionsMap.get(permissionsList[permissionCode])!!
        }
        else -> {
            permissionsMap.set(permissionsList[permissionCode], PermissionStatus.DENIED)
            permissionsMap.get(permissionsList[permissionCode])!!
        }
    }
}

fun isPermissionGranted(context: Context, permissionCode: Int): Boolean =
    ContextCompat.checkSelfPermission(context, permissionsList[permissionCode]) == PackageManager.PERMISSION_GRANTED

fun shouldRequestRational(activity: Activity, permissionCode: Int): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionsList[permissionCode])


/*
internal class PermissionRequest(
    private val context: Context,
    private val lifecycle: Lifecycle
) : DefaultLifecycleObserver {
    var permissionStatus: PermissionResult = PermissionResult.INIT

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)

        fun finalPermissionRequest(activity: Activity, permission: String) : PermissionResult {
            permissionStatus = when {
                isPermissionGranted(context, permission) -> {
                    PermissionResult.GRANTED
                }
                shouldRequestRational(activity, permission) -> {
                    PermissionResult.RATIONAL
                }
                else -> {
                    lastRequest()
                }
            }
            return permissionStatus
        }
    }
}
*/


// ? NEW
fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.shouldShowRationalFor(permission: String): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

@SuppressLint("ObsoleteSdkInt")
val ActivityRecognitionPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    android.Manifest.permission.ACTIVITY_RECOGNITION
} else {
    "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
}
