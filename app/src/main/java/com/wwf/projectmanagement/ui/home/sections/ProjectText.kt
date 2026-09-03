package com.wwf.projectmanagement.ui.home.sections

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.wwf.projectmanagement.data.Project
import com.wwf.projectmanagement.ui.components.MaxSubtitleWidth
import com.wwf.projectmanagement.ui.home.infoBodyColor
import com.wwf.projectmanagement.ui.home.infoHeadingColor

/** `.info-content h3`: 1.5rem, weight 700, #1a9641. */
@Composable
fun ProjectTitle(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
        color = infoHeadingColor(),
        textAlign = TextAlign.Center,
        modifier = modifier.semantics { heading() },
    )
}

/** `.info-content p`: 1.125rem, line-height 1.6, #555. Always occupies exactly [lines] lines. */
@Composable
fun ProjectDescription(project: Project, lines: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(project.descriptionRes),
        minLines = lines,
        maxLines = lines,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, lineHeight = 29.sp),
        color = infoBodyColor(),
        textAlign = TextAlign.Center,
        modifier = modifier.widthIn(max = MaxSubtitleWidth),
    )
}
