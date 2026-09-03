package com.wwf.projectmanagement.ui.home.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.R
import com.wwf.projectmanagement.ui.components.ActionButton
import com.wwf.projectmanagement.ui.components.ActionButtonStyle

/** Replaces the Login button on the home page once a session exists. */
@Composable
fun SignedInBanner(email: String, onLogoutClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.session_signed_in_as, email),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        ActionButton(
            text = stringResource(R.string.action_logout),
            onClick = onLogoutClick,
            style = ActionButtonStyle.Danger,
            icon = Icons.AutoMirrored.Filled.ExitToApp,
        )
    }
}
