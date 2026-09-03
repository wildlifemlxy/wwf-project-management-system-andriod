package com.wwf.projectmanagement.ui.home.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.components.GradientCtaButton
import com.wwf.projectmanagement.ui.components.HeroBadge
import com.wwf.projectmanagement.ui.components.HeroLogo
import com.wwf.projectmanagement.ui.components.HeroSubtitle
import com.wwf.projectmanagement.ui.components.HeroTitle
import com.wwf.projectmanagement.ui.components.LiveDateTime
import com.wwf.projectmanagement.ui.components.MaxSubtitleWidth
import com.wwf.projectmanagement.ui.components.reveal
import com.wwf.projectmanagement.ui.home.sections.LegalLinks
import com.wwf.projectmanagement.ui.home.sections.SignedInBanner

/**
 * Page 1 - title / hero page of the website (`.hero-content`). The Login button opens the sign-in
 * dialog; once signed in it is replaced by the signed-in banner with Logout.
 * Sections reveal one after another on first display.
 */
@Composable
fun HeroPage(
    signedInEmail: String?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTermsOfService: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val window = LocalWindowSize.current
    val gap = window.sectionSpacing

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.reveal(0)) { HeroBadge() }
        Spacer(Modifier.height(gap))
        Box(Modifier.reveal(1)) { HeroLogo() }
        Spacer(Modifier.height(gap))
        Box(Modifier.reveal(2)) { HeroTitle(stringResource(R.string.hero_title)) }
        Spacer(Modifier.height(12.dp))
        LiveDateTime(Modifier.reveal(3))
        Spacer(Modifier.height(gap))
        Box(Modifier.reveal(4).widthIn(max = MaxSubtitleWidth)) { HeroSubtitle(stringResource(R.string.hero_subtitle)) }
        Spacer(Modifier.height(gap))
        if (signedInEmail == null) {
            GradientCtaButton(
                text = stringResource(R.string.action_login),
                onClick = onLoginClick,
                modifier = Modifier.reveal(5).then(if (window.isCompactWidth) Modifier.fillMaxWidth() else Modifier),
            )
        } else {
            SignedInBanner(email = signedInEmail, onLogoutClick = onLogoutClick, modifier = Modifier.reveal(5))
        }
        Spacer(Modifier.height(24.dp))
        LegalLinks(onOpenPrivacyPolicy, onOpenTermsOfService, modifier = Modifier.reveal(6))
    }
}
