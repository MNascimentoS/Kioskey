package dev.mnascimento.kioskey.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.mnascimento.kioskey.Kioskey
import dev.mnascimento.kioskey.Kioskey.moveTaskToFront

class KioskeyObserver : Application.ActivityLifecycleCallbacks {

    private lateinit var windowManager: WindowManager
    private lateinit var interceptView: KioskeyViewGroup

    private var touchService: Intent? = null

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        if (KioskeySharedPreferences.isAppInKioskMode(activity).not()) return
        if (!KioskeySharedPreferences.isAppLaunched(activity)) {
            KioskeySharedPreferences.saveAppLaunched(activity, true)
        }
        Kioskey.lockUnlockDevice(activity, true)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val rootView = activity.findViewById<View>(android.R.id.content).rootView

        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        WindowInsetsControllerCompat(activity.window, rootView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setupDisableTotemListener(activity)
        lockStatusBar(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        activity.moveTaskToFront()
    }

    override fun onActivityDestroyed(activity: Activity) {
        activity.moveTaskToFront()
        unLockStatusBar()
    }

    private fun setupDisableTotemListener(activity: Activity) {
        if (touchService != null) return
        touchService = Intent(activity, KioskeyService::class.java)
        activity.startService(touchService)
    }

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    private fun lockStatusBar(activity: Activity) {
        val params = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
            }
            gravity = Gravity.TOP
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            width = WindowManager.LayoutParams.MATCH_PARENT

            val rectangle = Rect()
            activity.window?.decorView?.getWindowVisibleDisplayFrame(rectangle)
            val statusBarHeight: Int = rectangle.top
            val contentViewTop = activity.window?.findViewById<View>(Window.ID_ANDROID_CONTENT)?.top
            val result = contentViewTop?.minus(statusBarHeight)

            height = result ?: 50
            format = PixelFormat.TRANSPARENT
        }
        interceptView = KioskeyViewGroup(activity)
        try {
            windowManager.addView(interceptView, params)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    private fun unLockStatusBar() {
        if (!this::windowManager.isInitialized ||
            !this::interceptView.isInitialized || !interceptView.isShown
        ) {
            return
        }
        try {
            windowManager.removeView(interceptView)
        } catch (e: java.lang.RuntimeException) {
            e.printStackTrace()
        }
    }

}