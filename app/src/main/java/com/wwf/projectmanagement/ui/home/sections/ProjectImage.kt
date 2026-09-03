package com.wwf.projectmanagement.ui.home.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.wwf.projectmanagement.data.Project
import com.wwf.projectmanagement.ui.components.pressScale
import com.wwf.projectmanagement.ui.home.infoHeadingColor

/**
 * Project artwork sized as a fraction of the available width (web: `width:200px`, auto height)
 * while keeping the bitmap's intrinsic proportions, and shrunk further if that would exceed
 * [maxHeight]. Tapping the image opens the project, with press feedback.
 */
@Composable
fun ProjectImage(
    project: Project,
    name: String,
    widthFraction: Float,
    maxHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val painter = painterResource(project.imageRes)
    val ratio = painter.intrinsicSize.let { if (it.isSpecified && it.height > 0f) it.width / it.height else 1f }
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(16.dp)
    val glow = infoHeadingColor()
    BoxWithConstraints(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val width = min(maxWidth * widthFraction, maxHeight * ratio)
        Image(
            painter = painter,
            contentDescription = name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(width)
                .aspectRatio(ratio)
                .pressScale(interaction, pressedScale = 0.97f)
                .shadow(12.dp, shape, ambientColor = glow, spotColor = glow)
                .clip(shape)
                .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
        )
    }
}
