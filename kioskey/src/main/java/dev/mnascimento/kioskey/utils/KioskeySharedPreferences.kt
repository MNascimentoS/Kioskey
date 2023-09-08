package dev.mnascimento.kioskey.utils

import android.content.Context

object KioskeySharedPreferences {

    private const val KIOSKEY_PREF = "application_pref"
    private const val KIOSK_MAIN_SCREEN_PREF = "kiosk_main_screen_pref"
    private const val KIOSK_MODE_STATUS_PREF = "kiosk_mode_locked_pref"
    private const val KIOSK_PASSWORD_PREF = "kiosk_password_pref"
    private const val APP_LAUNCHED_PREF = "app_launched_pref"

    private fun readBoolean(context: Context, key: String): Boolean {
        val sharedPref = context.getSharedPreferences(KIOSKEY_PREF, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }

    private fun readString(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences(KIOSKEY_PREF, Context.MODE_PRIVATE)
        return sharedPref.getString(key, "")
    }

    private fun write(context: Context, key: String, value: Any) {
        val sharedPref = context.getSharedPreferences(KIOSKEY_PREF, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is String -> editor.putString(key, value)
        }
        editor.apply()
    }

    fun setKioskMode(context: Context, isLocked: Boolean) {
        write(context, KIOSK_MODE_STATUS_PREF, isLocked)
    }

    fun isAppInKioskMode(context: Context): Boolean {
        return readBoolean(context, KIOSK_MODE_STATUS_PREF)
    }

    fun saveAppLaunched(context: Context, isFirstLaunch: Boolean) {
        write(context, APP_LAUNCHED_PREF, isFirstLaunch)
    }

    fun isAppLaunched(context: Context): Boolean {
        return readBoolean(context, APP_LAUNCHED_PREF)
    }

    fun savePassword(context: Context, password: String?) {
        password?.let { write(context, KIOSK_PASSWORD_PREF, password) }
    }

    fun validatePassword(context: Context, password: String?): Boolean {
        val savedPassword = readString(context, KIOSK_PASSWORD_PREF)
        return password == savedPassword
    }

    fun saveMainScreen(context: Context, mainScreen: String) {
        write(context, KIOSK_MAIN_SCREEN_PREF, mainScreen)
    }

    fun getMaiScreen(context: Context): String? {
        return readString(context, KIOSK_MAIN_SCREEN_PREF)
    }
}