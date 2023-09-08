package dev.mnascimento.kioskey.utils

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.MATCH_PARENT
import android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
import android.view.WindowManager.LayoutParams.TYPE_PHONE
import android.widget.LinearLayout
import dev.mnascimento.kioskey.Kioskey
import dev.mnascimento.kioskey.R


class KioskeyService : Service(), OnTouchListener {
    private var mWindowManager: WindowManager? = null
    private var touchLayout: LinearLayout? = null

    private var clickCount = 0
    private var handler: Handler? = null

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        touchLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(BOX_SIZE, MATCH_PARENT)
            setOnTouchListener(this@KioskeyService)
        }

        val mParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LayoutParams(
                BOX_SIZE,
                MATCH_PARENT,
                TYPE_APPLICATION_OVERLAY,
                FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            LayoutParams(
                BOX_SIZE,
                MATCH_PARENT,
                TYPE_PHONE,
                FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }
        mParams.gravity = Gravity.END or Gravity.TOP

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        mWindowManager!!.addView(touchLayout, mParams)


    }

    override fun onDestroy() {
        if (mWindowManager != null) {
            if (touchLayout != null) mWindowManager!!.removeView(touchLayout)
        }
        super.onDestroy()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            clickCount++
            if (handler == null) {
                handler = Handler(Looper.getMainLooper())
                handler?.postDelayed({
                    if (clickCount > CLICK_COUNT_MIN) {
                        showInputPasswordDialog()
                    }
                    clickCount = 0
                    handler = null
                }, CLICK_TIMEOUT)
            }
        }
        v.performClick()
        return true
    }

    private fun showInputPasswordDialog() {
        val dialog = KioskeyUtils.createPasswordDialog(
            this,
            getString(R.string.kioskey_name),
            getString(R.string.kioskey_info_password_request),

        ) { password ->
            Kioskey.lockUnlockDevice(this, false, password)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.window?.setType(TYPE_APPLICATION_OVERLAY)
        } else {
            dialog.window?.setType(TYPE_PHONE)
        }
        dialog.show()

    }

    companion object {
        private const val BOX_SIZE = 50
        private const val CLICK_TIMEOUT = 1500L
        private const val CLICK_COUNT_MIN = 5
    }
}