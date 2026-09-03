package com.wwf.projectmanagement.ui.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.data.Project
import com.wwf.projectmanagement.data.Projects
import com.wwf.projectmanagement.ui.components.MaxContentWidth
import com.wwf.projectmanagement.ui.components.PageBackground
import com.wwf.projectmanagement.ui.components.WindowSizeProvider
import com.wwf.projectmanagement.ui.home.PagerControls
import com.wwf.projectmanagement.ui.home.SwipePage
import com.wwf.projectmanagement.ui.project.pages.ProjectGalleryPage
import com.wwf.projectmanagement.ui.project.pages.ProjectHeroPage
import com.wwf.projectmanagement.ui.project.pages.ProjectInfoPage
import com.wwf.projectmanagement.ui.project.pages.ProjectToolsPage
import com.wwf.projectmanagement.ui.theme.WwfTheme
import kotlinx.coroutines.launch

/**
 * Native port of a project page (`/StrawheadedBulbul`, `/RifleRangeRoad`) presented like the
 * home page: a horizontal swipe with one page per section, each in its own file under `pages/`:
 *
 *  1. [ProjectHeroPage]  - title, clock and CTAs
 *  2. [ProjectToolsPage]   - "Comprehensive Conservation Tools" feature cards
 *  3. [ProjectGalleryPage] - photo & video gallery streamed from the backend
 *  4. [ProjectInfoPage]    - background, statistics and painting
 *
 * Signed out, every action reads "Go to Login" and routes to the home page login
 * ([onGoToLogin]); signed in, the tools are available (Rifle Range Road's survey tools are
 * "Coming soon" like the web). There is no on-screen back button; the system back gesture
 * returns to the home page.
 */
@Composable
fun ProjectScreen(
    project: Project,
    isLoggedIn: Boolean,
    onGoToLogin: () -> Unit,
    onLogout: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenSurveyEvents: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val name = stringResource(project.nameRes)
    val pageCount = 4
    val pagerState = rememberPagerState { pageCount }
    val scope = rememberCoroutineScope()
    val goToPage: (Int) -> Unit = { target ->
        scope.launch { pagerState.animateScrollToPage(target.coerceIn(0, pageCount - 1)) }
    }

    WindowSizeProvider(modifier) {
        val insets = WindowInsets.safeDrawing.asPaddingValues()
        PageBackground {
            Column(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) { page ->
                    SwipePage(pagerState = pagerState, page = page) {
                        val pageModifier = Modifier.widthIn(max = MaxContentWidth).fillMaxWidth()
                        when (page) {
                            0 -> ProjectHeroPage(
                                projectName = name,
                                isLoggedIn = isLoggedIn,
                                surveyToolsAvailable = project.surveyToolsAvailable,
                                onLoginClick = onGoToLogin,
                                onLogoutClick = onLogout,
                                onOpenDashboard = onOpenDashboard,
                                onOpenSurveyEvents = onOpenSurveyEvents,
                                onOpenSettings = onOpenSettings,
                                modifier = pageModifier,
                            )
                            1 -> ProjectToolsPage(
                                projectName = name,
                                isLoggedIn = isLoggedIn,
                                surveyToolsAvailable = project.surveyToolsAvailable,
                                onLoginClick = onGoToLogin,
                                onOpenDashboard = onOpenDashboard,
                                onOpenSurveyEvents = onOpenSurveyEvents,
                                onOpenSettings = onOpenSettings,
                                modifier = pageModifier,
                            )
                            2 -> ProjectGalleryPage(projectId = project.id, modifier = pageModifier)
                            else -> ProjectInfoPage(projectId = project.id, info = project.info, modifier = pageModifier)
                        }
                    }
                }
                PagerControls(
                    pagerState = pagerState,
                    onGoToPage = goToPage,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp, bottom = insets.calculateBottomPadding() + 8.dp),
                )
            }
        }
    }
}

@Preview(name = "Signed out", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ProjectScreenSignedOutPreview() {
    WwfTheme {
        ProjectScreen(Projects.strawHeadedBulbul, isLoggedIn = false, {}, {}, {}, {}, {})
    }
}

@Preview(name = "Signed in", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ProjectScreenSignedInPreview() {
    WwfTheme {
        ProjectScreen(Projects.strawHeadedBulbul, isLoggedIn = true, {}, {}, {}, {}, {})
    }
}
