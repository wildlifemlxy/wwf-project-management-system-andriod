package com.wwf.projectmanagement.ui.home.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.ui.components.pressScale
import com.wwf.projectmanagement.ui.theme.CtaCyanEnd

/** "View <project>" link from the website: padding .5rem 1.5rem, radius 6px, #00B8EA, white, weight 600. */
@Composable
fun ViewProjectButton(name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.action_view_project, name)
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(6.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .pressScale(interaction, pressedScale = 0.96f)
            .shadow(6.dp, shape, spotColor = CtaCyanEnd)
            .clip(shape)
            .background(CtaCyanEnd)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .heightIn(min = 48.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .semantics { contentDescription = label },
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
            color = Color.White,
        )
    }
}
