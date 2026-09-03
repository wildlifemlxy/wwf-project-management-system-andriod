package com.wwf.projectmanagement.ui.home.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wwf.projectmanagement.data.Project
import com.wwf.projectmanagement.ui.LocalWindowSize
import com.wwf.projectmanagement.ui.components.reveal
import com.wwf.projectmanagement.ui.home.sections.ProjectDescription
import com.wwf.projectmanagement.ui.home.sections.ProjectImage
import com.wwf.projectmanagement.ui.home.sections.ProjectTitle
import com.wwf.projectmanagement.ui.home.sections.ViewProjectButton

/** Lines reserved for the description so the button sits at the same height on every project. */
private const val DescriptionLines = 5

/**
 * Pages 2..n - one project (`.info-section` on the website), laid straight onto the page
 * background with no card container. Every element is given a fixed slot (image height, one
 * title line, [DescriptionLines] description lines) so the picture, title, description and
 * button land in exactly the same position on each project page.
 */
@Composable
fun ProjectPage(
    project: Project,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val window = LocalWindowSize.current
    val name = stringResource(project.nameRes)
    val imageHeight = window.height * 0.42f

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(Modifier.fillMaxWidth().height(imageHeight), contentAlignment = Alignment.Center) {
            ProjectImage(
                project = project,
                name = name,
                widthFraction = if (window.isCompactWidth) 0.62f else 0.5f,
                maxHeight = imageHeight,
                onClick = onOpen,
                modifier = Modifier.reveal(0),
            )
        }
        Spacer(Modifier.height(24.dp))
        ProjectTitle(name = name, modifier = Modifier.reveal(1))
        Spacer(Modifier.height(16.dp))
        ProjectDescription(project = project, lines = DescriptionLines, modifier = Modifier.reveal(2))
        Spacer(Modifier.height(24.dp))
        ViewProjectButton(name = name, onClick = onOpen, modifier = Modifier.reveal(3))
    }
}
