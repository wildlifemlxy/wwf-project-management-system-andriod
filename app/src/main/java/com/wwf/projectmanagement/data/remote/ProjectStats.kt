package com.wwf.projectmanagement.data.remote

/** The four statistics tiles on a project's info page, already formatted for display. */
data class ProjectStats(
    val observations: String,
    val locations: String,
    val volunteers: String,
    val yearsActive: String,
)
