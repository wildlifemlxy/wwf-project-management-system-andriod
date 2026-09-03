package com.wwf.projectmanagement

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.wwf.projectmanagement.data.local.LocalSession
import com.wwf.projectmanagement.ui.navigation.AppNavHost
import com.wwf.projectmanagement.ui.theme.WwfTheme

/**
 * Single-activity host. Nothing is loaded from the web; login is an optional device-local session.
 *
 * Configuration changes (fold/unfold, window resize, theme) are handled by Compose re-measuring
 * against the new window size; the activity is locked to portrait.
 *
 * The app runs full screen: the Android navigation bar is hidden (immersive, swipe from the
 * edge to reveal it temporarily) and re-hidden whenever the window regains focus.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Gallery media must not leave the app: block screenshots and screen recording.
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        hideNavigationBar()
        val session = LocalSession(this)
        setContent {
            WwfTheme { AppNavHost(session) }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideNavigationBar()
    }

    private fun hideNavigationBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.navigationBars())
        }
    }
}
