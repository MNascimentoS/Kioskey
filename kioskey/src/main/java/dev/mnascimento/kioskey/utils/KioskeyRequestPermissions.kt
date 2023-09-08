package dev.mnascimento.kioskey.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

object KioskeyRequestPermissions {

    val permissions = listOf(
        Manifest.permission.REORDER_TASKS,
        Manifest.permission.SYSTEM_ALERT_WINDOW
    )

    fun checkAllPermissions(context: Context) : Boolean {
        var hasAllPermissions = false
        permissions.forEach { permission ->
            hasAllPermissions = checkPermission(context, permission)
            if (!hasAllPermissions) return@forEach
        }
        return hasAllPermissions
    }

    fun checkPermission(context: Context, permission: String) : Boolean {
        return when (permission) {
            Manifest.permission.SYSTEM_ALERT_WINDOW -> Settings.canDrawOverlays(context)
            Settings.ACTION_HOME_SETTINGS -> false
            else -> {
                ContextCompat.checkSelfPermission(context, permission) ==
                        PackageManager.PERMISSION_GRANTED
            }
        }
    }

    fun grantPermission(activity: ComponentActivity, permission: String) {
        when {
            checkPermission(activity, permission) -> {}
            permission == Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                if (!Settings.canDrawOverlays(activity)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${activity.packageName}")
                    )
                    activity.startActivity(intent)
                }
            }
            else -> {
                val requestPermissionLauncher = activity.registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { _ -> }
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}