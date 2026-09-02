package com.wwf.projectmanagement

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wwf.projectmanagement.ui.HomeScreen
import com.wwf.projectmanagement.ui.theme.WwfTheme

/**
 * AppCompatActivity gives us the XML/Views world (fragments, Material components,
 * okAppCompat theming back to API 24) while setContent hosts Jetpack Compose.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WwfTheme {
                HomeScreen()
            }
        }
    }
}
