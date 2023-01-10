package com.example.dailytracker.utilityModule

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

data class PermissionStatus(
    var activityRecognition: PermissionResult = PermissionResult.INIT
)

val permissionsMap = mapOf(
    "ACTIVITY_RECOGNITION" to android.Manifest.permission.ACTIVITY_RECOGNITION
)

enum class Permissions(val value: String) {
    ACTIVITY_RECOGNITION(android.Manifest.permission.ACTIVITY_RECOGNITION)
}

//Different states
enum class PermissionResult {
    INIT,
    GRANTED,
    RATIONAL,
    REQUEST,
    DENIED
}

/** Doc: https://developer.android.com/training/permissions/requesting**/
var permissionStatus: PermissionResult = PermissionResult.INIT

fun finalPermissionRequest(context: Context, activity: Activity, permission: String) : PermissionResult {
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

fun isPermissionGranted(context: Context, permission: String): Boolean =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

fun shouldRequestRational(activity: Activity, permission: String): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

fun lastRequest(): PermissionResult = PermissionResult.REQUEST