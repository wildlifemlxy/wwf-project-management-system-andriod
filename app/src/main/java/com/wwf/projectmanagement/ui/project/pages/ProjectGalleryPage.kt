package com.wwf.projectmanagement.ui.project.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wwf.projectmanagement.ui.project.sections.GallerySection

/** Page 3 of a project: backend photo/video gallery with media filters and a full-screen viewer. */
@Composable
fun ProjectGalleryPage(projectId: String, modifier: Modifier = Modifier) {
    GallerySection(projectId = projectId, modifier = modifier)
}
