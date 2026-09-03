package com.wwf.projectmanagement.ui.project.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wwf.projectmanagement.ui.project.sections.ProjectHeroSection

/** Page 1 of a project: badge, logo, "WWF <project> Survey Platform", clock and the CTAs. */
@Composable
fun ProjectHeroPage(
    projectName: String,
    isLoggedIn: Boolean,
    surveyToolsAvailable: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenSurveyEvents: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ProjectHeroSection(
        projectName = projectName,
        isLoggedIn = isLoggedIn,
        surveyToolsAvailable = surveyToolsAvailable,
        onLoginClick = onLoginClick,
        onLogoutClick = onLogoutClick,
        onOpenDashboard = onOpenDashboard,
        onOpenSurveyEvents = onOpenSurveyEvents,
        onOpenSettings = onOpenSettings,
        modifier = modifier,
    )
}
