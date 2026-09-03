package com.wwf.projectmanagement.ui.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.data.Project
import com.wwf.projectmanagement.data.Projects
import com.wwf.projectmanagement.ui.components.MaxContentWidth
import com.wwf.projectmanagement.ui.components.PageBackground
import com.wwf.projectmanagement.ui.components.WindowSizeProvider
import com.wwf.projectmanagement.ui.home.pages.HeroPage
import com.wwf.projectmanagement.ui.home.pages.ProjectPage
import com.wwf.projectmanagement.ui.theme.WwfTheme
import kotlinx.coroutines.launch

/**
 * Native port of the WWF Project Platform public home page (`/`), presented as an interactive,
 * dashboard-style horizontal swipe: page 1 is the title/hero page, followed by one centred page
 * per project. Pages fade/scale as they swipe, content reveals with a staggered entrance, every
 * tappable element gives press feedback, and the bottom strip offers tappable page dots.
 * Public; the hero page hosts the optional Login (device-local session). Portrait only; light and dark mode.
 *
 * Structure:
 *  - [SwipePage]      – per-page container (centring, scrolling, swipe transition)
 *  - `pages/`         – one file per page ([HeroPage], [ProjectPage])
 *  - `sections/`      – one file per building block used by the pages
 *  - [PagerControls]  – bottom page-dots strip
 */
@Composable
fun HomeScreen(
    onOpenProject: (Project) -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTermsOfService: () -> Unit,
    signedInEmail: String?,
    onLogin: (email: String, password: String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    /** Open the sign-in dialog straight away (used by "Go to Login" on project pages). */
    showLoginInitially: Boolean = false,
    projects: List<Project> = Projects.all,
) {
    var showLogin by rememberSaveable { mutableStateOf(showLoginInitially) }
    val pageCount = 1 + projects.size
    val pagerState = rememberPagerState { pageCount }
    val scope = rememberCoroutineScope()
    val goToPage: (Int) -> Unit = { target ->
        scope.launch { pagerState.animateScrollToPage(target.coerceIn(0, pageCount - 1)) }
    }

    WindowSizeProvider(modifier) {
        PageBackground {
            Column(Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) { page ->
                    SwipePage(pagerState = pagerState, page = page) {
                        val pageModifier = Modifier.widthIn(max = MaxContentWidth).fillMaxWidth()
                        if (page == 0) {
                            HeroPage(
                                signedInEmail = signedInEmail,
                                onLoginClick = { showLogin = true },
                                onLogoutClick = onLogout,
                                onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                                onOpenTermsOfService = onOpenTermsOfService,
                                modifier = pageModifier,
                            )
                        } else {
                            val project = projects[page - 1]
                            ProjectPage(
                                project = project,
                                onOpen = { onOpenProject(project) },
                                modifier = pageModifier,
                            )
                        }
                    }
                }
                PagerControls(
                    pagerState = pagerState,
                    onGoToPage = goToPage,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp, bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding() + 8.dp),
                )
            }
        }
        if (showLogin) {
            LoginDialog(
                onDismiss = { showLogin = false },
                onLogin = { email, password ->
                    showLogin = false
                    onLogin(email, password)
                },
            )
        }
    }
}

@Preview(name = "Phone", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(name = "Foldable", showBackground = true, widthDp = 673, heightDp = 841)
@Preview(name = "Tablet", showBackground = true, widthDp = 800, heightDp = 1280)
@Preview(name = "Phone dark", showBackground = true, widthDp = 360, heightDp = 800, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreview() {
    WwfTheme {
        HomeScreen(onOpenProject = {}, onOpenPrivacyPolicy = {}, onOpenTermsOfService = {}, signedInEmail = null, onLogin = { _, _ -> }, onLogout = {})
    }
}
