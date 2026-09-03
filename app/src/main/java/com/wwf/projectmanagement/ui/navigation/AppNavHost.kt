package com.wwf.projectmanagement.ui.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.data.Projects
import com.wwf.projectmanagement.data.local.LocalSession
import com.wwf.projectmanagement.ui.home.HomeScreen
import com.wwf.projectmanagement.ui.project.ProjectScreen

/**
 * Two-level app: the home page and one project page per project (`/` and `/StrawheadedBulbul`,
 * `/RifleRangeRoad` on the web). The selected project id is the only navigation state, so it
 * survives recreation via [rememberSaveable]; system back returns to home. Login lives on the
 * home page: "Go to Login" on a project page navigates home with the sign-in dialog open.
 */
@Composable
fun AppNavHost(session: LocalSession) {
    var projectId by rememberSaveable { mutableStateOf<String?>(null) }
    // Set by "Go to Login" on a project page: return home with the sign-in dialog open.
    var loginRequested by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val comingSoon = { Toast.makeText(context, R.string.coming_soon, Toast.LENGTH_SHORT).show() }

    BackHandler(enabled = projectId != null) { projectId = null }

    AnimatedContent(
        targetState = Projects.byId(projectId),
        transitionSpec = {
            val forward = targetState != null
            val enter = slideInHorizontally { if (forward) it / 4 else -it / 4 } + fadeIn()
            val exit = slideOutHorizontally { if (forward) -it / 4 else it / 4 } + fadeOut()
            enter togetherWith exit
        },
        label = "screen",
    ) { project ->
        if (project == null) {
            HomeScreen(
                signedInEmail = session.signedInEmail,
                onLogin = { email, _ -> session.login(email) },
                onLogout = session::logout,
                showLoginInitially = loginRequested,
                onOpenProject = {
                    loginRequested = false
                    projectId = it.id
                },
                onOpenPrivacyPolicy = comingSoon,
                onOpenTermsOfService = comingSoon,
            )
        } else {
            ProjectScreen(
                project = project,
                isLoggedIn = session.isLoggedIn,
                onGoToLogin = {
                    loginRequested = true
                    projectId = null
                },
                onLogout = session::logout,
                onOpenDashboard = comingSoon,
                onOpenSurveyEvents = comingSoon,
                onOpenSettings = comingSoon,
            )
        }
    }
}
