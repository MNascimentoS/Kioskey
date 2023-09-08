package dev.mnascimento.kioskey.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.mnascimento.kioskey.Kioskey
import dev.mnascimento.kioskey.ui.theme.KioskeyTheme
import dev.mnascimento.kioskey.utils.KioskeyUtils

class SampleMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KioskeyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        val context = LocalContext.current
        Column {
            TextButton(onClick = {
                Kioskey.requestAllPermissions(this@SampleMainActivity)
            }) {
                Text(text = "Requisitar permiossoes")
            }
            TextButton(onClick = {
                KioskeyUtils.createPasswordDialog(
                    context,
                    "Kioskey",
                    "Digite a senha do modo Kiosk",

                    ) { password ->
                    Kioskey.savePassword(context, password)
                }.show()
            }
            ) {
                Text(text = "Defina a senha do totem")
            }
            TextButton(onClick = {
                Kioskey.startTotem(this@SampleMainActivity, SampleTotemFirstActivity::class.java)
            }) {
                Text(text = "Iniciar Modo Totem")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        KioskeyTheme {
            Greeting("Android")
        }
    }
}
