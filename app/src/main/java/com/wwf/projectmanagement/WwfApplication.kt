package com.wwf.projectmanagement

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class WwfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Follow the system light/dark setting on every supported API level.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}
