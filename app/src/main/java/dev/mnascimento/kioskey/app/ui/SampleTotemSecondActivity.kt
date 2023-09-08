package dev.mnascimento.kioskey.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.mnascimento.kioskey.ui.theme.KioskeyTheme

class SampleTotemSecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KioskeyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.inversePrimary
                ) {
                    Greeting2("Terceira tela")
                }
            }
        }
    }

    @Composable
    fun Greeting2(name: String, modifier: Modifier = Modifier) {
        TextButton({
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:")
                }
            )
        }) {
            Text(text = "Other App")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun Greeting2Preview() {
        KioskeyTheme {
            Greeting2("Terceira tela")
        }
    }
}