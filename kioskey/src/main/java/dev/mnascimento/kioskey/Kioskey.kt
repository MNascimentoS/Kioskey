package dev.mnascimento.kioskey

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.PowerManager
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.getSystemService
import dev.mnascimento.kioskey.utils.KioskeyObserver
import dev.mnascimento.kioskey.utils.KioskeyRequestPermissions
import dev.mnascimento.kioskey.utils.KioskeySharedPreferences
import dev.mnascimento.kioskey.utils.KioskeyUtils
import dev.mnascimento.kioskey.utils.KioskeyViewGroup


object Kioskey {

    fun watch(context: Application) {
        context.registerActivityLifecycleCallbacks(KioskeyObserver())
    }

    fun requestAllPermissions(activity: ComponentActivity) {
        with(activity) {
            if (KioskeyRequestPermissions.checkAllPermissions(activity)) {
                Toast.makeText(
                    this,
                    getString(R.string.kioskey_toast_granted_permissions),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val container = LinearLayout(this)
                container.orientation = LinearLayout.VERTICAL
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.leftMargin = 50
                params.rightMargin = 50
                KioskeyRequestPermissions.permissions.forEach { permission ->
                    if (!KioskeyRequestPermissions.checkPermission(this, permission)) {
                        container.addView(
                            Button(this).apply {
                                text = getString(
                                    R.string.kioskey_info_permission_request_message,
                                    permission
                                )
                                layoutParams = params
                                setOnClickListener {
                                    KioskeyRequestPermissions.grantPermission(activity, permission)
                                }
                            }
                        )
                    }
                }
                KioskeyUtils.createDialog(
                    this,
                    getString(R.string.kioskey_name),
                    getString(R.string.kioskey_info_permission_request),
                    container
                ).show()
            }
        }
    }

    fun startTotem(context: Context, javaClass: Class<*>) {
        KioskeySharedPreferences.saveMainScreen(context, javaClass.name)
        lockUnlockDevice(context, true)
        context.startActivity(Intent(context, javaClass).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
            addCategory(Intent.CATEGORY_HOME)
            addCategory(Intent.CATEGORY_DESK_DOCK)
            addCategory(Intent.CATEGORY_MONKEY)
        })
    }

    fun savePassword(context: Context, password: String? = null) {
        KioskeySharedPreferences.savePassword(context, password)
    }

    internal fun lockUnlockDevice(context: Context, lockTotem: Boolean, password: String? = null) {
        with(context) {
            if (lockTotem) {
                KioskeySharedPreferences.savePassword(this, password)
//                lockStatusBar()
                KioskeySharedPreferences.setKioskMode(this, true)
            } else {
                if (KioskeySharedPreferences.validatePassword(this, password)) {
                    Toast.makeText(
                        this,
                        getString(R.string.kioskey_toast_exiting_totem),
                        Toast.LENGTH_SHORT
                    ).show()
//                    unLockStatusBar()
                    KioskeySharedPreferences.setKioskMode(context, false)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.kioskey_error_validate_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    internal fun Activity.moveTaskToFront() {
        if (!KioskeySharedPreferences.isAppInKioskMode(this)) return
        val mainScreen = KioskeySharedPreferences.getMaiScreen(this)
        when {
            isDestroyed && mainScreen == javaClass.name -> {
                startTotem(this, this::class.java)
            }

            else -> {
                (applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                    .moveTaskToFront(taskId, ActivityManager.MOVE_TASK_WITH_HOME)
            }
        }
    }

//    private fun Context.lockStatusBar() {
//        windowManager = applicationContext
//            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val params = WindowManager.LayoutParams().apply {
//            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else {
//                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
//            }
//            gravity = Gravity.TOP
//            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//            width = WindowManager.LayoutParams.MATCH_PARENT
//
//            val rectangle = Rect()
//            val window = (this@lockStatusBar as? Activity)?.window
//            window?.decorView?.getWindowVisibleDisplayFrame(rectangle)
//            val statusBarHeight: Int = rectangle.top
//            val contentViewTop = window?.findViewById<View>(Window.ID_ANDROID_CONTENT)?.top
//            val result = contentViewTop?.minus(statusBarHeight)
//
//            height = result ?: 0
//            format = PixelFormat.TRANSPARENT
//        }
//        interceptView = KioskeyViewGroup(this)
//        try {
//            windowManager.addView(interceptView, params)
//        } catch (e: RuntimeException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun unLockStatusBar() {
//        if (!this::windowManager.isInitialized ||
//            !this::interceptView.isInitialized || !interceptView.isShown
//        ) {
//            return
//        }
//        try {
//            windowManager.removeView(interceptView)
//        } catch (e: java.lang.RuntimeException) {
//            e.printStackTrace()
//        }
//    }

}