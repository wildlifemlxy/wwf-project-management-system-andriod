package com.wwf.projectmanagement.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.WindowHeightClass
import com.wwf.projectmanagement.ui.theme.PrimaryGreen

@Composable
fun HeroLogo() {
    val window = LocalWindowSize.current
    val shortWindow = window.heightClass == WindowHeightClass.Compact
    // `.hero-logo-enhanced img` is 160px on mobile up to 320px on desktop.
    val logoHeight: Dp = if (shortWindow) 120.dp else window.scaled(150.dp, 220.dp, 280.dp)
    val cardPadding = if (shortWindow) 12.dp else window.scaled(16.dp, 28.dp, 40.dp)
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .shadow(16.dp, shape, ambientColor = PrimaryGreen, spotColor = PrimaryGreen)
            .clip(shape)
            .background(Color.White)
            .border(4.dp, PrimaryGreen.copy(alpha = 0.20f), shape)
            .padding(cardPadding),
    ) {
        Image(
            painter = painterResource(R.drawable.wwf_logo_large),
            contentDescription = stringResource(R.string.hero_logo_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(logoHeight),
        )
    }
}
