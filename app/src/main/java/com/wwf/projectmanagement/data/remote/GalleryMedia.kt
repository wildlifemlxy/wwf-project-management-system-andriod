package com.wwf.projectmanagement.data.remote

/** One photo or video in a project's gallery. */
data class GalleryMedia(
    /** Unique key: the backend file id (Straw-headed Bulbul) or the image URL (Rifle Range Road). */
    val id: String,
    val title: String,
    val mimeType: String,
    /**
     * Direct URL the file is fetched from with a plain GET (Rifle Range Road survey photos hosted
     * on Typeform). `null` means the file comes from `POST /gallery {purpose: "stream", fileId}`.
     */
    val url: String? = null,
) {
    val isVideo: Boolean get() = mimeType.startsWith("video/")
}
