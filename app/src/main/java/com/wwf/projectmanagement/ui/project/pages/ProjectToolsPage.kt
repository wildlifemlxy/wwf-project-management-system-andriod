package com.wwf.projectmanagement.ui.project.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wwf.projectmanagement.ui.project.sections.FeaturesSection

/** Page 2 of a project: "Comprehensive Conservation Tools" feature cards. */
@Composable
fun ProjectToolsPage(
    projectName: String,
    isLoggedIn: Boolean,
    surveyToolsAvailable: Boolean,
    onLoginClick: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenSurveyEvents: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FeaturesSection(
        projectName = projectName,
        isLoggedIn = isLoggedIn,
        surveyToolsAvailable = surveyToolsAvailable,
        onLoginClick = onLoginClick,
        onOpenDashboard = onOpenDashboard,
        onOpenSurveyEvents = onOpenSurveyEvents,
        onOpenSettings = onOpenSettings,
        modifier = modifier,
    )
}
