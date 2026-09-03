package com.wwf.projectmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wwf.projectmanagement.ui.theme.ForestGreen
import com.wwf.projectmanagement.ui.theme.ForestGreenDark
import com.wwf.projectmanagement.ui.theme.PrimaryBlue
import com.wwf.projectmanagement.ui.theme.PrimaryBlueDark
import com.wwf.projectmanagement.ui.theme.PrimaryGreen
import com.wwf.projectmanagement.ui.theme.PrimaryGreenDark

/** Web `.btn` variants used by the project page CTAs. */
enum class ActionButtonStyle(internal val colors: List<Color>) {
    /** `.btn-primary`: green gradient. */
    Primary(listOf(PrimaryGreen, PrimaryGreenDark)),
    /** `.btn-accent`: forest-green gradient. */
    Accent(listOf(ForestGreen, ForestGreenDark)),
    /** `.btn-secondary`: blue gradient. */
    Secondary(listOf(PrimaryBlue, PrimaryBlueDark)),
    /** `.btn-logout`: red gradient. */
    Danger(listOf(Color(0xFFF87171), Color(0xFFB91C1C))),
}

/**
 * Gradient pill button with optional leading icon and press feedback. `enabled = false` renders
 * the web's "Coming soon" state (greyed out, not clickable).
 */
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ActionButtonStyle = ActionButtonStyle.Primary,
    /** Overrides the gradient (e.g. per-feature-card colours on the web); defaults to [style]. */
    colors: List<Color> = style.colors,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    disabledHint: String? = null,
    /** Smaller text/padding for tight layouts such as the three-in-a-row tool cards on phones. */
    compact: Boolean = false,
) {
    val shape = RoundedCornerShape(8.dp)
    val interaction = remember { MutableInteractionSource() }
    val brush = if (enabled) Brush.linearGradient(colors)
    else Brush.linearGradient(listOf(Color(0xFF9CA3AF), Color(0xFF9CA3AF)))
    val description = if (!enabled && disabledHint != null) "$text, $disabledHint" else text

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .pressScale(interaction, pressedScale = 0.97f)
            .alpha(if (enabled) 1f else 0.65f)
            .shadow(if (enabled) 6.dp else 0.dp, shape, spotColor = colors.first())
            .clip(shape)
            .background(brush)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .heightIn(min = if (compact) 40.dp else 48.dp)
            .padding(horizontal = if (compact) 8.dp else 20.dp, vertical = if (compact) 8.dp else 12.dp)
            .semantics { contentDescription = description },
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(if (compact) 14.dp else 18.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = if (compact) 12.sp else 15.sp,
                lineHeight = if (compact) 15.sp else 20.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}
