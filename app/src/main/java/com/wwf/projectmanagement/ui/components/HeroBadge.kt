package com.wwf.projectmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.ui.theme.PrimaryGreen

@Composable
fun HeroBadge(text: String = stringResource(R.string.hero_badge)) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(PrimaryGreen.copy(alpha = 0.10f))
            .border(1.dp, PrimaryGreen.copy(alpha = 0.20f), CircleShape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_conservation_badge),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp),
        )
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
    }
}
