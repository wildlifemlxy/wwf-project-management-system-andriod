package com.wwf.projectmanagement.ui.project.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wwf.projectmanagement.data.ProjectInfo
import com.wwf.projectmanagement.ui.project.sections.InfoSection

/** Page 3 of a project: background copy, statistics tiles and the painting with its caption. */
@Composable
fun ProjectInfoPage(projectId: String, info: ProjectInfo, modifier: Modifier = Modifier) {
    InfoSection(projectId = projectId, info = info, modifier = modifier)
}
