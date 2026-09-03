package com.wwf.projectmanagement.ui.project.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.components.ActionButton
import com.wwf.projectmanagement.ui.components.ActionButtonStyle
import com.wwf.projectmanagement.ui.components.GradientCtaButton
import com.wwf.projectmanagement.ui.components.HeroBadge
import com.wwf.projectmanagement.ui.components.HeroLogo
import com.wwf.projectmanagement.ui.components.HeroSubtitle
import com.wwf.projectmanagement.ui.components.HeroTitle
import com.wwf.projectmanagement.ui.components.LiveDateTime
import com.wwf.projectmanagement.ui.components.MaxSubtitleWidth
import com.wwf.projectmanagement.ui.components.reveal

/**
 * Project hero (`.hero-section` on `/StrawheadedBulbul`): badge, logo, "WWF <name> Survey
 * Platform", live clock, subtitle and the state-dependent CTA row.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectHeroSection(
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
    val window = LocalWindowSize.current
    val gap = window.sectionSpacing

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.reveal(0)) { HeroBadge() }
        Spacer(Modifier.height(gap))
        Box(Modifier.reveal(1)) { HeroLogo() }
        Spacer(Modifier.height(gap))
        Box(Modifier.reveal(2)) { HeroTitle(stringResource(R.string.project_hero_title, projectName)) }
        Spacer(Modifier.height(12.dp))
        LiveDateTime(Modifier.reveal(3))
        Spacer(Modifier.height(gap))
        Box(Modifier.reveal(4).widthIn(max = MaxSubtitleWidth)) { HeroSubtitle(stringResource(R.string.project_hero_subtitle)) }
        Spacer(Modifier.height(gap))

        if (isLoggedIn) {
            // `.hero-cta`: wraps on wide screens, stacks in a column on phones.
            val comingSoon = stringResource(R.string.label_coming_soon)
            FlowRow(
                modifier = Modifier.reveal(5),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = if (window.isCompactWidth) 1 else 4,
            ) {
                val item = if (window.isCompactWidth) Modifier.fillMaxWidth() else Modifier
                ActionButton(
                    text = stringResource(R.string.action_explore_dashboard),
                    onClick = onOpenDashboard,
                    style = ActionButtonStyle.Primary,
                    icon = Icons.Default.Star,
                    modifier = item,
                )
                ActionButton(
                    text = stringResource(R.string.action_survey_event_management),
                    onClick = onOpenSurveyEvents,
                    style = ActionButtonStyle.Accent,
                    icon = Icons.Default.Email,
                    enabled = surveyToolsAvailable,
                    disabledHint = comingSoon,
                    modifier = item,
                )
                ActionButton(
                    text = stringResource(R.string.action_settings),
                    onClick = onOpenSettings,
                    style = ActionButtonStyle.Secondary,
                    icon = Icons.Default.Settings,
                    enabled = surveyToolsAvailable,
                    disabledHint = comingSoon,
                    modifier = item,
                )
                ActionButton(
                    text = stringResource(R.string.action_logout),
                    onClick = onLogoutClick,
                    style = ActionButtonStyle.Danger,
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    modifier = item,
                )
            }
        } else {
            // Public preview: `.btn-login-cta` "Go to Login".
            GradientCtaButton(
                text = stringResource(R.string.action_go_to_login),
                onClick = onLoginClick,
                icon = Icons.Default.AccountCircle,
                modifier = Modifier.reveal(5).then(if (window.isCompactWidth) Modifier.fillMaxWidth() else Modifier),
            )
        }
    }
}
