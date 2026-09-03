package com.wwf.projectmanagement.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.coroutineContext

/*
 * The WWF backend's gallery endpoints, mirroring the web client:
 *
 *  Straw-headed Bulbul (`getGalleryImages()` / `streamImage()`):
 *  - `POST /gallery {purpose: "gallery"}` lists every photo and video.
 *  - `POST /gallery {purpose: "stream", fileId}` returns the raw file (an image or a video, per its Content-Type).
 *
 *  Rifle Range Road (`getRifleRangeRoadSurveyData()`):
 *  - `POST /rifleRangeRoad/surveys {purpose: "retrieve"}` returns survey records; the gallery is
 *    the unique `Image URL` of each record, fetched with a plain GET.
 *
 * The API is public (no auth headers), and files are only served as a whole, so [stream] reads
 * the response into memory while reporting progress like the web's `onDownloadProgress`.
 * Nothing is ever written to disk.
 */
class GalleryApi(private val baseUrl: String = BASE_URL) {

    /** Straw-headed Bulbul gallery: every photo and video uploaded to the backend. */
    suspend fun listStrawHeadedBulbul(): List<GalleryMedia> = withContext(Dispatchers.IO) {
        val connection = openJsonPost("$baseUrl/gallery", JSONObject().put("purpose", "gallery"), timeoutMs = LIST_TIMEOUT_MS)
        try {
            connection.checkOk()
            val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            val images = json.optJSONArray("images") ?: return@withContext emptyList()
            List(images.length()) { i ->
                val item = images.getJSONObject(i)
                GalleryMedia(
                    id = item.getString("id"),
                    title = item.optString("title").ifBlank { item.optString("alt") },
                    mimeType = item.optString("mimeType"),
                )
            }
        } finally {
            connection.disconnect()
        }
    }

    /** Rifle Range Road gallery: the distinct photo URLs attached to survey records. */
    suspend fun listRifleRangeRoad(): List<GalleryMedia> = withContext(Dispatchers.IO) {
        val connection = openJsonPost(
            "$baseUrl/rifleRangeRoad/surveys",
            JSONObject().put("purpose", "retrieve"),
            timeoutMs = LIST_TIMEOUT_MS,
        )
        try {
            connection.checkOk()
            val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            val surveys = json.optJSONArray("surveys") ?: return@withContext emptyList()
            val urls = LinkedHashSet<String>()
            for (i in 0 until surveys.length()) {
                val url = surveys.optJSONObject(i)?.optString("Image URL")?.trim().orEmpty()
                if (url.startsWith("http://") || url.startsWith("https://")) urls += url
            }
            urls.map { url ->
                GalleryMedia(
                    id = url,
                    title = url.substringAfterLast('/').substringBefore('?'),
                    mimeType = if (url.substringAfterLast('.', "").lowercase() in VIDEO_EXTENSIONS) "video/mp4" else "image/jpeg",
                    url = url,
                )
            }
        } finally {
            connection.disconnect()
        }
    }

    /**
     * Fetches [media] straight from the server into memory. [onProgress] receives 0..100 as
     * bytes arrive, or `null` when the server doesn't announce the size (Typeform sends no
     * Content-Length).
     */
    suspend fun stream(media: GalleryMedia, onProgress: (Int?) -> Unit = {}): ByteArray =
        withContext(Dispatchers.IO) {
            val connection = media.url?.let { openGet(it, timeoutMs = STREAM_TIMEOUT_MS) }
                ?: openJsonPost(
                    "$baseUrl/gallery",
                    JSONObject().put("purpose", "stream").put("fileId", media.id),
                    timeoutMs = STREAM_TIMEOUT_MS,
                )
            try {
                connection.checkOk()
                val total = connection.contentLengthLong
                onProgress(if (total > 0) 0 else null)
                val output = ByteArrayOutputStream(if (total > 0) total.toInt() else 1024 * 1024)
                var loaded = 0L
                var lastPercent = -1
                connection.inputStream.use { input ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        coroutineContext.ensureActive()
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        loaded += read
                        if (total > 0) {
                            val percent = (loaded * 100 / total).toInt()
                            if (percent != lastPercent) {
                                lastPercent = percent
                                onProgress(percent)
                            }
                        }
                    }
                }
                if (output.size() == 0) throw IOException("Empty file received from server")
                onProgress(100)
                output.toByteArray()
            } finally {
                connection.disconnect()
            }
        }

    private fun openGet(url: String, timeoutMs: Int): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = CONNECT_TIMEOUT_MS
        connection.readTimeout = timeoutMs
        connection.instanceFollowRedirects = true
        return connection
    }

    private fun openJsonPost(url: String, body: JSONObject, timeoutMs: Int): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = CONNECT_TIMEOUT_MS
        connection.readTimeout = timeoutMs
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.use { it.write(body.toString().toByteArray()) }
        return connection
    }

    private fun HttpURLConnection.checkOk() {
        if (responseCode !in 200..299) throw IOException("Server responded with HTTP $responseCode")
    }

    companion object {
        const val BASE_URL = "https://shb-backend.azurewebsites.net"
        private const val CONNECT_TIMEOUT_MS = 30_000
        private const val LIST_TIMEOUT_MS = 60_000
        /** Same 10 minute ceiling as the web client uses for large videos. */
        private const val STREAM_TIMEOUT_MS = 600_000
        private val VIDEO_EXTENSIONS = setOf("mp4", "mov", "m4v", "webm", "3gp")
    }
}