package com.wwf.projectmanagement.data.local

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

/**
 * Device-local, simulated session: any email + password signs in. Persisted in SharedPreferences
 * so the state survives restarts. Swap for a real auth backend later without touching the UI.
 */
class LocalSession(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var signedInEmail: String? by mutableStateOf(prefs.getString(KEY_EMAIL, null))
        private set

    val isLoggedIn: Boolean get() = signedInEmail != null

    fun login(email: String) {
        prefs.edit { putString(KEY_EMAIL, email) }
        signedInEmail = email
    }

    fun logout() {
        prefs.edit { remove(KEY_EMAIL) }
        signedInEmail = null
    }

    private companion object {
        const val PREFS = "wwf_session"
        const val KEY_EMAIL = "email"
    }
}
