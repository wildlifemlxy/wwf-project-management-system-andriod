package com.wwf.projectmanagement

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.wwf.projectmanagement.data.remote.GalleryRepository
import com.wwf.projectmanagement.data.remote.StatsRepository

class WwfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Follow the system light/dark setting on every supported API level.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        // Start streaming every gallery photo and video into memory so they're ready to view.
        GalleryRepository.get().prefetchAll()
        StatsRepository.get().prefetchAll()
    }
}
