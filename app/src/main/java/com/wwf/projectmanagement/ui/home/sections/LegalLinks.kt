package com.wwf.projectmanagement.ui.home.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.ui.components.isDarkTheme
import com.wwf.projectmanagement.ui.theme.LinkBlue
import com.wwf.projectmanagement.ui.theme.PrimaryBlue

/** Privacy Policy | Terms of Service footer links (`.hero-legal-links`). */
@Composable
fun LegalLinks(onOpenPrivacyPolicy: () -> Unit, onOpenTermsOfService: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
        LinkText(stringResource(R.string.link_privacy_policy), onOpenPrivacyPolicy)
        Text(stringResource(R.string.link_separator), color = MaterialTheme.colorScheme.onSurfaceVariant)
        LinkText(stringResource(R.string.link_terms_of_service), onOpenTermsOfService)
    }
}

@Composable
private fun LinkText(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline,
        ),
        color = if (isDarkTheme()) PrimaryBlue else LinkBlue,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(role = Role.Button, onClick = onClick)
            // 44dp touch target without changing visual size.
            .heightIn(min = 44.dp)
            .padding(horizontal = 4.dp)
            .wrapContentHeight(Alignment.CenterVertically),
    )
}
