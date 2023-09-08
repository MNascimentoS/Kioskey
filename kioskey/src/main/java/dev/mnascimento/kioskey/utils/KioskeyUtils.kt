package dev.mnascimento.kioskey.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import dev.mnascimento.kioskey.R

object KioskeyUtils {

    fun createDialog(
        context: Context,
        title: String,
        message: String,
        container: View? = null
    ): AlertDialog = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setView(container)
        }.create()

    fun createPasswordDialog(
        context: Context,
        title: String,
        message: String,
        callback: (String) -> Unit
    ): AlertDialog = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            val input = EditText(context)
            input.inputType =
                InputType.TYPE_NUMBER_VARIATION_PASSWORD or InputType.TYPE_CLASS_NUMBER

            val container = FrameLayout(context)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = 50
            params.rightMargin = 50
            input.layoutParams = params
            container.addView(input)

            setView(container)
            setPositiveButton(
                context.getString(R.string.kioskey_confirm_button)
            ) { _, _ ->
                callback(input.text.toString())
            }
            setNegativeButton(
                context.getString(R.string.kioskey_cancel_button)
            ) { dialog, _ -> dialog.cancel() }
        }.create()

}