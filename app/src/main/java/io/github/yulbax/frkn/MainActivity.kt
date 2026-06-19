package io.github.yulbax.frkn

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import io.github.yulbax.frkn.ui.screens.MainScreen
import io.github.yulbax.frkn.ui.theme.FRKNTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FRKNTheme {
                MainScreen()
            }
        }
    }
}
