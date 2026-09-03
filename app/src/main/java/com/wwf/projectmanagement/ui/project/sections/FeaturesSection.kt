package com.wwf.projectmanagement.ui.project.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.components.ActionButton
import com.wwf.projectmanagement.ui.components.isDarkTheme
import com.wwf.projectmanagement.ui.components.reveal

/** One `.feature-card` on the web: icon gradient, accent colours and copy. */
private data class Feature(
    val titleRes: Int,
    val bullets: List<Int>,
    val icon: ImageVector,
    val iconGradient: List<Color>,
    val accent: Color,
    val actionRes: Int,
)

private val Dashboard = Feature(
    titleRes = R.string.feature_dashboard_title,
    bullets = listOf(R.string.feature_dashboard_1, R.string.feature_dashboard_2, R.string.feature_dashboard_3),
    icon = Icons.Default.Star,
    iconGradient = listOf(Color(0xFF4ADE80), Color(0xFF22D3EE)),
    accent = Color(0xFF22C55E),
    actionRes = R.string.action_view_dashboard,
)
private val Survey = Feature(
    titleRes = R.string.feature_survey_title,
    bullets = listOf(R.string.feature_survey_1, R.string.feature_survey_2, R.string.feature_survey_3, R.string.feature_survey_4),
    icon = Icons.Default.Email,
    iconGradient = listOf(Color(0xFF818CF8), Color(0xFFF472B6)),
    accent = Color(0xFF6366F1),
    actionRes = R.string.action_manage_surveys,
)
private val Telegram = Feature(
    titleRes = R.string.feature_telegram_title,
    bullets = listOf(R.string.feature_telegram_1, R.string.feature_telegram_2, R.string.feature_telegram_3),
    icon = Icons.AutoMirrored.Filled.Send,
    iconGradient = listOf(Color(0xFF229ED9), Color(0xFF0A5C8C)),
    accent = Color(0xFF229ED9),
    actionRes = R.string.action_telegram_settings,
)

/**
 * `.features-section`: "Comprehensive Conservation Tools" header and the three feature cards.
 * Signed out, every card's button reads "Go to Login"; signed in, it opens the tool (or is
 * disabled as "Coming soon" when [surveyToolsAvailable] is false).
 */
@Composable
fun FeaturesSection(
    projectName: String,
    isLoggedIn: Boolean,
    surveyToolsAvailable: Boolean,
    onLoginClick: () -> Unit,
    onOpenDashboard: () -> Unit,
    onOpenSurveyEvents: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val window = LocalWindowSize.current

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.features_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = if (window.isCompactWidth) 24.sp else 36.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(0).semantics { heading() },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.features_subtitle, projectName),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = if (window.isCompactWidth) 14.sp else 17.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.reveal(1).widthIn(max = 600.dp),
        )
        Spacer(Modifier.height(if (window.isCompactWidth) 16.dp else window.sectionSpacing))

        // Three cards in one row whenever they'd be at least MinCardWidth wide, otherwise two per
        // row. Cards in a row share the same height so the buttons line up regardless of how
        // much text each card has; the card metrics scale down with the available width.
        val cards: List<@Composable (CardMetrics, Modifier) -> Unit> = listOf(
            { metrics, m -> FeatureCard(Dashboard, metrics, isLoggedIn, enabled = true, onLoginClick, onOpenDashboard, m.reveal(2)) },
            { metrics, m -> FeatureCard(Survey, metrics, isLoggedIn, enabled = surveyToolsAvailable, onLoginClick, onOpenSurveyEvents, m.reveal(3)) },
            { metrics, m -> FeatureCard(Telegram, metrics, isLoggedIn, enabled = surveyToolsAvailable, onLoginClick, onOpenSettings, m.reveal(4)) },
        )
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val gap = if (maxWidth < 480.dp) 8.dp else CardGap
            val columns = if ((maxWidth - gap * 2) / 3 >= MinCardWidth) 3 else 2
            val metrics = CardMetrics.forCardWidth((maxWidth - gap * (columns - 1)) / columns)
            Column(verticalArrangement = Arrangement.spacedBy(gap), modifier = Modifier.fillMaxWidth()) {
                cards.chunked(columns).forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    ) {
                        row.forEach { card -> card(metrics, Modifier.weight(1f).fillMaxHeight()) }
                        repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

/** Narrowest a card may be in a three-up row before the section falls back to two per row. */
private val MinCardWidth = 90.dp
private val CardGap = 20.dp

/** Card dimensions for a given card width: full web sizing on tablets, compact on phones. */
private data class CardMetrics(
    val compact: Boolean,
    val paddingH: Dp,
    val paddingV: Dp,
    val iconBox: Dp,
    val icon: Dp,
    val titleSp: TextUnit,
    val bulletSp: TextUnit,
    val bulletLineSp: TextUnit,
    val corner: Dp,
) {
    companion object {
        fun forCardWidth(width: Dp): CardMetrics = when {
            width < 150.dp -> CardMetrics(true, 8.dp, 12.dp, 36.dp, 20.dp, 13.sp, 11.sp, 14.sp, 12.dp)
            width < 240.dp -> CardMetrics(true, 12.dp, 16.dp, 48.dp, 26.dp, 16.sp, 13.sp, 17.sp, 14.dp)
            else -> CardMetrics(false, 24.dp, 28.dp, 64.dp, 32.dp, 20.sp, 14.sp, 21.sp, 18.dp)
        }
    }
}

@Composable
private fun FeatureCard(
    feature: Feature,
    metrics: CardMetrics,
    isLoggedIn: Boolean,
    enabled: Boolean,
    onLoginClick: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dark = isDarkTheme()
    val shape = RoundedCornerShape(metrics.corner)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .shadow(if (dark) 0.dp else 10.dp, shape, spotColor = Color.Black.copy(alpha = 0.25f))
            .clip(shape)
            .background(if (dark) Color.White.copy(alpha = 0.06f) else Color.White)
            .border(1.dp, if (dark) Color.White.copy(alpha = 0.12f) else Color(0xFFE0E7EF), shape)
            .padding(horizontal = metrics.paddingH, vertical = metrics.paddingV),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(metrics.iconBox)
                .clip(RoundedCornerShape(metrics.iconBox / 4.5f))
                .background(Brush.linearGradient(feature.iconGradient)),
        ) {
            Icon(feature.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(metrics.icon))
        }
        Spacer(Modifier.height(if (metrics.compact) 10.dp else 18.dp))
        Text(
            text = stringResource(feature.titleRes),
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = metrics.titleSp,
                lineHeight = metrics.titleSp * 1.2f,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(if (metrics.compact) 8.dp else 12.dp))
        val bulletStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = metrics.bulletSp, lineHeight = metrics.bulletLineSp)
        Column(verticalArrangement = Arrangement.spacedBy(if (metrics.compact) 4.dp else 8.dp), modifier = Modifier.fillMaxWidth()) {
            feature.bullets.forEach { bullet ->
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(if (metrics.compact) 4.dp else 8.dp)) {
                    Text("•", color = feature.accent, style = bulletStyle.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = stringResource(bullet),
                        style = bulletStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f).heightIn(min = if (metrics.compact) 12.dp else 24.dp))
        if (isLoggedIn) {
            ActionButton(
                text = stringResource(feature.actionRes),
                onClick = onOpen,
                modifier = Modifier.fillMaxWidth(),
                colors = listOf(feature.accent, feature.accent),
                enabled = enabled,
                disabledHint = stringResource(R.string.label_coming_soon),
                compact = metrics.compact,
            )
        } else {
            ActionButton(
                text = stringResource(R.string.action_go_to_login),
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                colors = listOf(feature.accent, feature.accent),
                compact = metrics.compact,
            )
        }
    }
}
